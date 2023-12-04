package com.hxh19950701.webviewtvlive

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hxh19950701.webviewtvlive.playlist.Playlist.Companion.firstChannel
import com.hxh19950701.webviewtvlive.playlist.PlaylistManager
import com.hxh19950701.webviewtvlive.widget.ChannelPlayerView
import com.hxh19950701.webviewtvlive.widget.PlaylistView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    companion object {
        enum class UiMode {
            STANDARD, CHANNELS, CHANNEL_SETTINGS, SETTINGS
        }
    }

    private lateinit var playerView: ChannelPlayerView
    private lateinit var mainLayout: FrameLayout
    private lateinit var playlistView: PlaylistView
    private lateinit var loadingView: TextView
    private var isDestroyed = false

    private var uiMode = UiMode.STANDARD
        set(value) {
            if (field == value) return
            field = value
            when (value) {
                UiMode.STANDARD -> {
                    playlistView.visibility = View.GONE
                }

                UiMode.CHANNELS -> {
                    playlistView.visibility = View.VISIBLE
                }

                UiMode.CHANNEL_SETTINGS -> {
                    playlistView.visibility = View.GONE
                }

                else -> {}
            }

        }

    private val backToStandardModeAction = { uiMode = UiMode.STANDARD }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainLayout = findViewById(R.id.mainLayout)
        playerView = findViewById(R.id.player)
        playlistView = findViewById(R.id.playlist)
        loadingView = findViewById(R.id.loadingView)

        playlistView.onChannelSelectCallback = {
            preference.edit().putString(LAST_CHANNEL, it.name).apply()
            playerView.channel = it
            playlistView.post { uiMode = UiMode.STANDARD }
        }
        playerView.activity = this
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        CoroutineScope(Dispatchers.Main).launch {
            val playlist = withContext(Dispatchers.IO) { PlaylistManager.loadPlaylist() }
            if (!isDestroyed) {
                playlistView.playlist = playlist
                loadingView.visibility = View.GONE
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

    override fun onPause() {
        super.onPause()
        uiMode = UiMode.STANDARD
    }

    override fun onDestroy() {
        isDestroyed = true
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (uiMode != UiMode.STANDARD) uiMode = UiMode.STANDARD
        else finish()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        repostBackToStandardModeAction()
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.x < playerView.width / 2) {
                uiMode = if (uiMode == UiMode.STANDARD) UiMode.CHANNELS else UiMode.STANDARD
            } else {
                uiMode = if (uiMode == UiMode.STANDARD) UiMode.CHANNEL_SETTINGS else UiMode.STANDARD
            }
        }
        return super.onTouchEvent(event)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        repostBackToStandardModeAction()
        when (uiMode) {
            UiMode.CHANNELS -> {
                if (playlistView.dispatchKeyEvent(event)) {
                    return true
                }
            }

            else -> {}
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        when (uiMode) {
            UiMode.STANDARD -> {
                when (event.keyCode) {
                    KeyEvent.KEYCODE_DPAD_UP -> playlistView.previousChannel()
                    KeyEvent.KEYCODE_DPAD_DOWN -> playlistView.nextChannel()
                    KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_ENTER -> uiMode = UiMode.CHANNELS
                }
            }

            else -> {}
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun repostBackToStandardModeAction() {
        playerView.removeCallbacks(backToStandardModeAction)
        playerView.postDelayed(backToStandardModeAction, 5000L)
    }
}