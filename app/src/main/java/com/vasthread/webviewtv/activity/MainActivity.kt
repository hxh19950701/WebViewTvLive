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
import com.vasthread.webviewtv.settings.SettingsManager
import com.vasthread.webviewtv.widget.AppSettingsView
import com.vasthread.webviewtv.widget.ChannelPlayerView
import com.vasthread.webviewtv.widget.ChannelSettingsView
import com.vasthread.webviewtv.widget.ExitConfirmView
import com.vasthread.webviewtv.widget.PlaylistView
import me.jessyan.autosize.AutoSize

class MainActivity : AppCompatActivity() {

    companion object {
        private const val LAST_CHANNEL = "last_channel"

        private enum class UiMode { STANDARD, CHANNELS, EXIT_CONFIRM, APP_SETTINGS, CHANNEL_SETTINGS }

        private const val OPERATION_TIMEOUT = 5000L
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
    private lateinit var uiLayout: FrameLayout
    private lateinit var playlistView: PlaylistView
    private lateinit var exitConfirmView: ExitConfirmView
    private lateinit var channelSettingsView: ChannelSettingsView
    private lateinit var appSettingsView: AppSettingsView

    private var lastChannel: Channel? = null

    private var uiMode = UiMode.STANDARD
        set(value) {
            if (field == value) return
            field = value
            playlistView.visibility = if (value == UiMode.CHANNELS) View.VISIBLE else View.GONE
            exitConfirmView.visibility = if (value == UiMode.EXIT_CONFIRM) View.VISIBLE else View.GONE
            channelSettingsView.visibility = if (value == UiMode.CHANNEL_SETTINGS) View.VISIBLE else View.GONE
            appSettingsView.visibility = if (value == UiMode.APP_SETTINGS) View.VISIBLE else View.GONE
            if (value == UiMode.STANDARD) {
                playerView.requestFocus()
            }
        }

    private val backToStandardModeAction = Runnable { uiMode = UiMode.STANDARD }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
        setupListener()
        initChannels()
    }

    @Suppress("DEPRECATION")
    private fun setupUi() {
        setContentView(R.layout.activity_main)
        mainLayout = findViewById(R.id.mainLayout)
        uiLayout = findViewById(R.id.uiLayout)
        playerView = findViewById(R.id.player)
        playlistView = findViewById(R.id.playlist)
        exitConfirmView = findViewById(R.id.exitConfirm)
        channelSettingsView = findViewById(R.id.channelSettings)
        appSettingsView = findViewById(R.id.appSettings)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
    }

    private fun setupListener() {
        playlistView.onChannelSelectCallback = {
            preference.edit().putString(LAST_CHANNEL, "${it.groupName}, ${it.name}").apply()
            playerView.channel = it
            channelSettingsView.setSelectedChannelSource(
                SettingsManager.getChannelLastSourceIndex(it.name), it.urls.size)
            playlistView.post { uiMode = UiMode.STANDARD }
        }
        channelSettingsView.onAspectRatioSelected = {
            playerView.setVideoRatio(it)
            uiMode = UiMode.STANDARD
        }
        channelSettingsView.onChannelSourceSelected = {
            val channel = playerView.channel!!
            val currentSource = SettingsManager.getChannelLastSourceIndex(channel.name)
            if (currentSource != it) {
                SettingsManager.setChannelLastSourceIndex(channel.name, it)
                playerView.refreshChannel()
                channelSettingsView.setSelectedChannelSource(it, channel.urls.size)
            }
            uiMode = UiMode.STANDARD
        }
        channelSettingsView.onGetVideoSize = { playerView.getVideoSize() }
        exitConfirmView.onUserSelection = {
            if (it == ExitConfirmView.Selection.EXIT) finish() else uiMode = UiMode.APP_SETTINGS
        }
        playerView.dismissAllViewCallback = { uiMode = UiMode.STANDARD }
        playerView.clickCallback = { x, _ ->
            val channelSettingsWidth = channelSettingsView.layoutParams.width + uiLayout.paddingRight
            uiMode = if (uiMode == UiMode.STANDARD)
                if (x < playerView.width - channelSettingsWidth) UiMode.CHANNELS else UiMode.CHANNEL_SETTINGS
            else
                UiMode.STANDARD
        }
        playerView.onVideoRatioChanged = { channelSettingsView.setSelectedAspectRatio(it) }
        playerView.onChannelReload = {
            channelSettingsView.setSelectedChannelSource(
                SettingsManager.getChannelLastSourceIndex(it.name), it.urls.size)
        }
        PlaylistManager.onPlaylistChange = { runOnUiThread { playlistView.playlist = it } }
        PlaylistManager.onUpdatePlaylistJobStateChange = {
            runOnUiThread { playlistView.updating = it}
        }
    }

    private fun initChannels() {
        playlistView.playlist = PlaylistManager.loadPlaylist()
        runCatching {
            val s = preference.getString(LAST_CHANNEL, "")!!
            val pair = s.split(", ").let { Pair(it[0], it[1]) }
            lastChannel = playlistView.playlist!!.getAllChannels()
                .firstOrNull { it.groupName == pair.first && it.name == pair.second }
        }
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
        if (lastChannel != null && playerView.channel == null) {
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

    override fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
        onMotionEvent(event)
        return super.dispatchGenericMotionEvent(event)
    }

    override fun dispatchPopulateAccessibilityEvent(event: AccessibilityEvent): Boolean {
        repostBackToStandardModeAction()
        return super.dispatchPopulateAccessibilityEvent(event)
    }

    override fun dispatchTrackballEvent(event: MotionEvent): Boolean {
        onMotionEvent(event)
        return super.dispatchTrackballEvent(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        onMotionEvent(event)
        return super.dispatchTouchEvent(event)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        onKeyEvent(event)
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
            UiMode.CHANNEL_SETTINGS -> if (channelSettingsView.dispatchKeyEvent(event)) return true
            UiMode.APP_SETTINGS -> if (appSettingsView.dispatchKeyEvent(event)) return true
            else -> {
                if (event.action == KeyEvent.ACTION_UP) {
                    when (keyCode) {
                        KeyEvent.KEYCODE_DPAD_UP -> playlistView.previousChannel()
                        KeyEvent.KEYCODE_DPAD_DOWN -> playlistView.nextChannel()
                        KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> uiMode = UiMode.CHANNELS
                        KeyEvent.KEYCODE_MENU -> uiMode = UiMode.CHANNEL_SETTINGS
                    }
                }
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun onKeyEvent(event: KeyEvent) {
        if (event.eventTime - event.downTime >= 1000L || event.action == KeyEvent.ACTION_UP) {
            repostBackToStandardModeAction()
        }
    }

    private fun onMotionEvent(event: MotionEvent) {
        if (event.eventTime - event.downTime >= 1000L || event.action == KeyEvent.ACTION_UP) {
            repostBackToStandardModeAction()
        }
    }

    private fun repostBackToStandardModeAction() {
        playerView.removeCallbacks(backToStandardModeAction)
        playerView.postDelayed(backToStandardModeAction, OPERATION_TIMEOUT)
    }

}