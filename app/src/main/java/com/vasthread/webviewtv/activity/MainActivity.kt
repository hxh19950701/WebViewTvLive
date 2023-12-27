package com.vasthread.webviewtv.activity

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.vasthread.webviewtv.R
import com.vasthread.webviewtv.misc.preference
import com.vasthread.webviewtv.playlist.Channel
import com.vasthread.webviewtv.playlist.Playlist.Companion.firstChannel
import com.vasthread.webviewtv.playlist.PlaylistManager
import com.vasthread.webviewtv.widget.ChannelPlayerView
import com.vasthread.webviewtv.widget.ExitConfirmView
import com.vasthread.webviewtv.widget.PlaylistView
import com.vasthread.webviewtv.widget.SettingsView
import me.jessyan.autosize.AutoSize

class MainActivity : AppCompatActivity() {

    companion object {
        private const val LAST_CHANNEL = "last_channel"

        private enum class UiMode { STANDARD, CHANNELS, EXIT_CONFIRM, SETTINGS }

        private val OPERATION_TIMEOUT = 5000L
        private val OPERATION_KEYS = arrayOf(
            KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_DPAD_DOWN,
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_MENU,
            KeyEvent.KEYCODE_BACK
        )
    }

    private lateinit var playerView: ChannelPlayerView
    private lateinit var mainLayout: FrameLayout
    private lateinit var playlistView: PlaylistView
    private lateinit var exitConfirmView: ExitConfirmView
    private lateinit var settingsView: SettingsView
    private var lastChannel: Channel? = null

    private var uiMode = UiMode.STANDARD
        set(value) {
            if (field == value) return
            field = value
            playlistView.visibility = if (value == UiMode.CHANNELS) View.VISIBLE else View.GONE
            exitConfirmView.visibility = if (value == UiMode.EXIT_CONFIRM) View.VISIBLE else View.GONE
            settingsView.visibility = if (value == UiMode.SETTINGS) View.VISIBLE else View.GONE
            if (value == UiMode.STANDARD) {
                playerView.requestFocus()
            }
        }

    private val backToStandardModeAction = Runnable { uiMode = UiMode.STANDARD }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
        setupListener()
        playlistView.playlist = PlaylistManager.loadPlaylist()
        initLastChannel()
    }

    @Suppress("DEPRECATION")
    private fun setupUi() {
        setContentView(R.layout.activity_main)
        mainLayout = findViewById(R.id.mainLayout)
        playerView = findViewById(R.id.player)
        playlistView = findViewById(R.id.playlist)
        exitConfirmView = findViewById(R.id.exitConfirm)
        settingsView = findViewById(R.id.settings)

        val visibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        window.decorView.systemUiVisibility = visibility
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun setupListener() {
        playlistView.onChannelSelectCallback = {
            preference.edit().putString(LAST_CHANNEL, it.name).apply()
            playerView.channel = it
            playlistView.post { uiMode = UiMode.STANDARD }
        }
        exitConfirmView.onUserSelection = {
            if (it == ExitConfirmView.Selection.EXIT) finish() else uiMode = UiMode.SETTINGS
        }
        playerView.dismissAllViewCallback = { uiMode = UiMode.STANDARD }
        playerView.setOnClickListener { uiMode = if (uiMode == UiMode.STANDARD) UiMode.CHANNELS else UiMode.STANDARD }
        PlaylistManager.onPlaylistChange = { runOnUiThread { playlistView.playlist = it } }
    }

    private fun initLastChannel() {
        val lastChannelName = preference.getString(LAST_CHANNEL, null)
        lastChannel = playlistView.playlist!!.getAllChannels().firstOrNull { it.name == lastChannelName }
        if (lastChannel == null) {
            lastChannel = playlistView.playlist.firstChannel()
        }
    }

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        AutoSize.autoConvertDensityOfGlobal(this)
        return super.onCreateView(parent, name, context, attrs)
    }

    override fun onResume() {
        super.onResume()
        if (lastChannel != null) {
            playlistView.currentChannel = lastChannel
        }
    }

    override fun onPause() {
        super.onPause()
        uiMode = UiMode.STANDARD
        lastChannel = playerView.channel
        playerView.channel = null
    }

    override fun onDestroy() {
        playerView.channel = null
        PlaylistManager.onPlaylistChange = null
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        uiMode = if (uiMode == UiMode.STANDARD) UiMode.EXIT_CONFIRM else UiMode.STANDARD
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            playerView.requestFocus()
        }
    }

    override fun dispatchGenericMotionEvent(ev: MotionEvent): Boolean {
        repostBackToStandardModeAction()
        return super.dispatchGenericMotionEvent(ev)
    }

    override fun dispatchPopulateAccessibilityEvent(event: AccessibilityEvent): Boolean {
        repostBackToStandardModeAction()
        return super.dispatchPopulateAccessibilityEvent(event)
    }

    override fun dispatchTrackballEvent(ev: MotionEvent): Boolean {
        repostBackToStandardModeAction()
        return super.dispatchTrackballEvent(ev)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        repostBackToStandardModeAction()
        return super.dispatchTouchEvent(ev)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        repostBackToStandardModeAction()
        val keyCode = event.keyCode
        if (!OPERATION_KEYS.contains(keyCode)) {
            return false
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return super.dispatchKeyEvent(event)
        }
        when (uiMode) {
            UiMode.CHANNELS -> if (playlistView.dispatchKeyEvent(event)) return true
            UiMode.EXIT_CONFIRM -> if (exitConfirmView.dispatchKeyEvent(event)) return true
            UiMode.SETTINGS -> if (settingsView.dispatchKeyEvent(event)) return true
            else -> {
                if (event.action == KeyEvent.ACTION_UP) {
                    when (keyCode) {
                        KeyEvent.KEYCODE_DPAD_UP -> playlistView.previousChannel()
                        KeyEvent.KEYCODE_DPAD_DOWN -> playlistView.nextChannel()
                        KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> uiMode = UiMode.CHANNELS
                    }
                }
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun repostBackToStandardModeAction() {
        playerView.removeCallbacks(backToStandardModeAction)
        playerView.postDelayed(backToStandardModeAction, OPERATION_TIMEOUT)
    }
}