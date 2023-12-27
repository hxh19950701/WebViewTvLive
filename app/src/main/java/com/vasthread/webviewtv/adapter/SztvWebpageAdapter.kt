package com.vasthread.webviewtv.adapter

import com.vasthread.webviewtv.widget.WebpageAdapterWebView

class SztvWebpageAdapter : CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("sztv.com.cn")

    override suspend fun enterFullscreen(webView: WebpageAdapterWebView) {
        enterFullscreenByPressKey(webView)
    }
}