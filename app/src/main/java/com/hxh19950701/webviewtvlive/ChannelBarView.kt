package com.hxh19950701.webviewtvlive

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView

class ChannelBarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val tvChannelName: TextView
    private val tvChannelUrl: TextView
    private val tvProgress: TextView

    init {
        setBackgroundResource(R.drawable.bg)
        LayoutInflater.from(context).inflate(R.layout.widget_channel_bar, this)
        tvChannelName = findViewById(R.id.tvChannelName)
        tvChannelUrl = findViewById(R.id.tvChannelUrl)
        tvProgress = findViewById(R.id.tvProgress)
        dismiss()
    }

    fun setCurrentChannelAndShow(channel: Channel) {
        tvChannelName.text = channel.name
        tvChannelUrl.text = channel.url
        visibility = VISIBLE
    }

    fun dismiss() {
        visibility = GONE
    }

    fun setProgress(progress: Int) {
        tvProgress.text = "(Load $progress%)"
    }
}