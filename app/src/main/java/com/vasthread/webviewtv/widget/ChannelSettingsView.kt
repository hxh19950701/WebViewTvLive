package com.vasthread.webviewtv.widget

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.vasthread.webviewtv.R

class ChannelSettingsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val rbAspectRatio_16_9: Button
    private val rbAspectRatio_4_3: Button
    private val tvVideoSize: TextView

    var onAspectRatioSelected: ((Boolean) -> Unit)? = null
    var onGetVideoSize: (() -> Point)? = null

    init {
        orientation = VERTICAL
        setBackgroundResource(R.drawable.bg)
        gravity = Gravity.CENTER
        LayoutInflater.from(context).inflate(R.layout.widget_channel_settings, this)

        rbAspectRatio_16_9 = findViewById(R.id.rbAspectRatio_16_9)
        rbAspectRatio_4_3 = findViewById(R.id.rbAspectRatio_4_3)
        tvVideoSize = findViewById(R.id.tvVideoSize)

        rbAspectRatio_16_9.setOnClickListener { onAspectRatioSelected?.invoke(true) }
        rbAspectRatio_4_3.setOnClickListener { onAspectRatioSelected?.invoke(false) }

        setSelectedAspectRatio(true)
    }

    fun setSelectedAspectRatio(is_16_9: Boolean) {
        rbAspectRatio_16_9.isSelected = is_16_9
        rbAspectRatio_4_3.isSelected = !is_16_9
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == VISIBLE) {
            onGetVideoSize?.invoke()?.let { tvVideoSize.text = if (it.x == 0 || it.y == 0) "未知" else "${it.x}x${it.y}" }
            post { (if (rbAspectRatio_16_9.isSelected) rbAspectRatio_16_9 else rbAspectRatio_4_3).requestFocus() }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_MENU && event.action == KeyEvent.ACTION_UP) {
            onAspectRatioSelected?.invoke(rbAspectRatio_16_9.isSelected)
        }
        return super.dispatchKeyEvent(event)
    }

}