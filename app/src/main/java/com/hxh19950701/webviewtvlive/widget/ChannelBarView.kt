package com.hxh19950701.webviewtvlive.widget

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.hxh19950701.webviewtvlive.playlist.Channel
import com.hxh19950701.webviewtvlive.R

class ChannelBarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val MIN_SHOW_DURATION = 5000L
    }

    private val tvChannelName: TextView
    private val tvChannelUrl: TextView
    private val tvProgress: TextView
    private var showTime = 0L

    private val dismissAction = Runnable { visibility = GONE }

    init {
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
        visibility = VISIBLE
        showTime = SystemClock.uptimeMillis()
    }

    fun requestDismiss() {
        if (SystemClock.uptimeMillis() - showTime > MIN_SHOW_DURATION) {
            dismissAction.run()
        } else {
            postDelayed(dismissAction, showTime + MIN_SHOW_DURATION - SystemClock.uptimeMillis())
        }
    }

    fun setProgress(progress: Int) {
        tvProgress.text = "$progress%"
    }
}