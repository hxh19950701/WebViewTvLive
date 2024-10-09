package com.vasthread.webviewtv.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.vasthread.webviewtv.R
import com.vasthread.webviewtv.playlist.Channel

class ChannelBarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val DISMISS_DELAY = 3000L
    }

    private val tvChannelName: TextView
    private val tvChannelUrl: TextView
    private val tvProgress: TextView

    private val dismissAction = Runnable { visibility = GONE }

    init {
        isClickable = true
        isFocusable = false
        setBackgroundResource(R.drawable.bg)
        LayoutInflater.from(context).inflate(R.layout.widget_channel_bar, this)
        tvChannelName = findViewById(R.id.tvChannelName)
        tvChannelUrl = findViewById(R.id.tvChannelUrl)
        tvProgress = findViewById(R.id.tvProgress)
        visibility = GONE
    }

    fun setCurrentChannelAndShow(channel: Channel) {
        removeCallbacks(dismissAction)
        tvChannelName.text = channel.name
        tvChannelUrl.text = channel.url
        setProgress(0)
        visibility = VISIBLE
    }

    fun dismiss() {
        removeCallbacks(dismissAction)
        visibility = GONE
    }

    fun setProgress(progress: Int) {
        removeCallbacks(dismissAction)
        tvProgress.text = "$progress%"
        if (progress == 100) {
            postDelayed(dismissAction, DISMISS_DELAY)
        }
    }
}