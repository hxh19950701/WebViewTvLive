package com.hxh19950701.webviewtvlive.adapter

import com.hxh19950701.webviewtvlive.widget.WebpageAdapterWebView

class YoutubeWebpageAdapter : CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("youtube.com")

    override fun userAgent() = null

    override suspend fun enterFullscreen(webView: WebpageAdapterWebView) {
        enterFullscreenByPressKey(webView)
    }
}