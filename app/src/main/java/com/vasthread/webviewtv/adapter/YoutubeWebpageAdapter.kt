package com.vasthread.webviewtv.adapter

import com.vasthread.webviewtv.widget.WebpageAdapterWebView

class YoutubeWebpageAdapter : CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("youtube.com")

    override fun userAgent() = null

    override suspend fun enterFullscreen(webView: WebpageAdapterWebView) {
        enterFullscreenByPressKey(webView)
    }
}