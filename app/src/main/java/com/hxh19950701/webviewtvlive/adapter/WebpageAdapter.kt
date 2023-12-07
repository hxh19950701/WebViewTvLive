package com.hxh19950701.webviewtvlive.adapter

import android.graphics.Point
import android.view.KeyEvent
import android.view.MotionEvent
import com.hxh19950701.webviewtvlive.widget.WebpageAdapterWebView

abstract class WebpageAdapter {

    companion object {
        internal const val TAG = "WebpageAdapter"
    }

    private var tryingEnterFullscreen = false

    abstract fun isBlockNetworkImage(): Boolean

    abstract fun userAgent(): String?

    abstract fun isAdaptedUrl(url: String): Boolean

    abstract fun javascript(): String

    suspend fun tryEnterFullscreen(webView: WebpageAdapterWebView) {
        if (!tryingEnterFullscreen) {
            tryingEnterFullscreen = true
            enterFullscreen(webView)
            tryingEnterFullscreen = false
        }
    }

    abstract suspend fun enterFullscreen(webView: WebpageAdapterWebView)

}
