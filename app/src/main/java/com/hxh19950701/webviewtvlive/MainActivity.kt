package com.hxh19950701.webviewtvlive

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.hxh19950701.webviewtvlive.Playlist.Companion.firstChannel

class MainActivity : Activity() {

    companion object {
        enum class UiMode {
            STANDARD, CHANNELS, CHANNEL_SETTINGS, SETTINGS
        }
    }

    private lateinit var playerView: ChannelPlayerView
    private lateinit var mainLayout: FrameLayout
    private lateinit var playlistView: PlaylistView

    private var uiMode = UiMode.STANDARD
        set(value) {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainLayout = findViewById(R.id.mainLayout)
        playerView = findViewById(R.id.player)
        playlistView = findViewById(R.id.playlist)

        playlistView.playlist = Playlist.createFromAllChannels("d", channels)
        playlistView.onChannelSelectCallback = {
            preference.edit().putString(LAST_CHANNEL, it.name).apply()
            playerView.channel = it
            playlistView.post { uiMode = UiMode.STANDARD }
        }
        playerView.activity = this
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        autoPlay()
    }

    private fun autoPlay() {
        if (playlistView.playlist != null) {
            val lastChannelName = preference.getString(LAST_CHANNEL, null)
            val lastChannel = playlistView.playlist!!.getAllChannels().firstOrNull { it.name == lastChannelName }
            if (lastChannel != null) {
                playlistView.currentChannel = lastChannel
            } else{
                playlistView.playlist.firstChannel()?.let {
                    playlistView.currentChannel = it
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        uiMode = UiMode.STANDARD
    }

    override fun onBackPressed() {
        if (uiMode != UiMode.STANDARD) uiMode = UiMode.STANDARD
        else finish()
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
        when (uiMode) {
            UiMode.CHANNELS -> {
                if (playlistView.dispatchKeyEvent(event)){
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
                    KeyEvent.KEYCODE_MENU -> uiMode = UiMode.CHANNELS
                }
            }

            else -> {}
        }
        return super.onKeyUp(keyCode, event)
    }
}