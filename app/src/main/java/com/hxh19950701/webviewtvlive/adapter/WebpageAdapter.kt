package com.hxh19950701.webviewtvlive.adapter

import android.util.Log
import com.hxh19950701.webviewtvlive.widget.WebpageAdapterWebView

abstract class WebpageAdapter {

    companion object {
        internal const val TAG = "WebpageAdapter"
    }

    private var tryingEnterFullscreen = false

    open fun userAgent(): String? = null

    abstract fun isAdaptedUrl(url: String): Boolean

    abstract fun javascript(): String

    suspend fun tryEnterFullscreen(webView: WebpageAdapterWebView) {
        if (tryingEnterFullscreen) {
            Log.i(TAG, "Repeated calls.")
        } else {
            tryingEnterFullscreen = true
            enterFullscreen(webView)
            tryingEnterFullscreen = false
        }
    }

    abstract suspend fun enterFullscreen(webView: WebpageAdapterWebView)

}
