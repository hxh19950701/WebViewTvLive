package com.hxh19950701.webviewtvlive.activity

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hxh19950701.webviewtvlive.R
import com.hxh19950701.webviewtvlive.misc.preference
import com.hxh19950701.webviewtvlive.playlist.Channel
import com.hxh19950701.webviewtvlive.playlist.Playlist.Companion.firstChannel
import com.hxh19950701.webviewtvlive.playlist.PlaylistManager
import com.hxh19950701.webviewtvlive.widget.ChannelPlayerView
import com.hxh19950701.webviewtvlive.widget.ExitConfirmView
import com.hxh19950701.webviewtvlive.widget.PlaylistView
import com.hxh19950701.webviewtvlive.widget.SettingsView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.jessyan.autosize.AutoSize

class MainActivity : AppCompatActivity() {

    companion object {
        private const val LAST_CHANNEL = "last_channel"
        private val KEYS = arrayOf(
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

    enum class UiMode { STANDARD, CHANNELS, EXIT_CONFIRM, SETTINGS }

    private lateinit var playerView: ChannelPlayerView
    private lateinit var mainLayout: FrameLayout
    private lateinit var playlistView: PlaylistView
    private lateinit var loadingPlaylistView: TextView
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

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainLayout = findViewById(R.id.mainLayout)
        playerView = findViewById(R.id.player)
        playlistView = findViewById(R.id.playlist)
        loadingPlaylistView = findViewById(R.id.loadingPlaylist)
        exitConfirmView = findViewById(R.id.exitConfirm)
        settingsView = findViewById(R.id.settings)

        playlistView.onChannelSelectCallback = {
            preference.edit().putString(LAST_CHANNEL, it.name).apply()
            playerView.channel = it
            playlistView.post { uiMode = UiMode.STANDARD }
        }
        exitConfirmView.onUserSelection = {
            if (it == ExitConfirmView.Selection.EXIT) finish() else uiMode = UiMode.SETTINGS
        }
        playerView.activity = this
        playerView.dismissAllViewCallback = { uiMode = UiMode.STANDARD }
        playerView.setOnClickListener { uiMode = if (uiMode == UiMode.STANDARD) UiMode.CHANNELS else UiMode.STANDARD }

        val visibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        window.decorView.systemUiVisibility = visibility
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        CoroutineScope(Dispatchers.Main).launch {
            val playlist = withContext(Dispatchers.IO) { PlaylistManager.loadPlaylist() }
            if (!isDestroyed) {
                playlistView.playlist = playlist
                loadingPlaylistView.visibility = View.GONE
                autoPlay()
            }
        }
    }

    private fun autoPlay() {
        if (playlistView.playlist != null) {
            val lastChannelName = preference.getString(LAST_CHANNEL, null)
            val lastChannel = playlistView.playlist!!.getAllChannels().firstOrNull { it.name == lastChannelName }
            if (lastChannel != null) {
                playlistView.currentChannel = lastChannel
            } else {
                playlistView.playlist.firstChannel()?.let { playlistView.currentChannel = it }
            }
        }
    }

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        AutoSize.autoConvertDensityOfGlobal(this)
        return super.onCreateView(parent, name, context, attrs)
    }

    override fun onResume() {
        super.onResume()
        if (lastChannel != null) {
            playerView.channel = lastChannel
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
        if (!KEYS.contains(keyCode)) {
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
        playerView.postDelayed(backToStandardModeAction, 5000L)
    }
}