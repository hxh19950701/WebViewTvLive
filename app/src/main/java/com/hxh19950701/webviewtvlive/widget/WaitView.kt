package com.hxh19950701.webviewtvlive.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.hxh19950701.webviewtvlive.R
import com.tencent.smtt.sdk.WebView

class WaitView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val RELOAD_DELAY = 10000L
    }

    var webView: WebView? = null

    private val reloadAction = Runnable {
        webView?.reload()
    }

    init {
        setBackgroundResource(R.drawable.bg)
        LayoutInflater.from(context).inflate(R.layout.widget_waiting, this)
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        removeCallbacks(reloadAction)
        if (visibility == VISIBLE) {
            postDelayed(reloadAction, RELOAD_DELAY)
        }
    }

}