package com.vasthread.webviewtv.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import com.tencent.smtt.sdk.WebView
import com.vasthread.webviewtv.R
import com.vasthread.webviewtv.misc.application
import com.vasthread.webviewtv.settings.SettingsManager

class WaitingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var playerView: ChannelPlayerView? = null

    private val reloadAction = Runnable {
        Toast.makeText(application, R.string.toast_reload_channel, Toast.LENGTH_SHORT).show()
        playerView?.apply {
            val channelName = channel!!.name
            var index = SettingsManager.getChannelLastSourceIndex(channelName) + 1
            if (index >= channel!!.urls.size) index = 0
            SettingsManager.setChannelLastSourceIndex(channelName, index)
            refreshChannel()
        }
    }

    init {
        isClickable = true
        setBackgroundResource(R.drawable.bg)
        LayoutInflater.from(context).inflate(R.layout.widget_waiting, this)
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        removeCallbacks(reloadAction)
        if (visibility == VISIBLE) {
            postDelayed(reloadAction, SettingsManager.getMaxLoadingTime() * 1000L)
        }
    }

}