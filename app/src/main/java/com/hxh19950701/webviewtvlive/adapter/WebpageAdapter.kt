package com.hxh19950701.webviewtvlive.adapter

import android.graphics.Point
import android.view.KeyEvent
import android.view.MotionEvent
import com.hxh19950701.webviewtvlive.widget.WebpageAdapterWebView

abstract class WebpageAdapter {

    companion object {
        internal const val TAG = "WebpageAdapter"
    }

    abstract fun userAgent(): String?

    abstract fun isAdaptedUrl(url: String): Boolean

    abstract fun javascript(): String

    abstract suspend fun enterFullscreen(webView: WebpageAdapterWebView)

}
