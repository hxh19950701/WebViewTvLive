package com.vasthread.webviewtv.adapter

import com.vasthread.webviewtv.widget.WebpageAdapterWebView

class ChromeUrlWebpageAdapter: WebpageAdapter() {
    override fun isAdaptedUrl(url: String) = url.startsWith("chrome:") || url.startsWith("about:")

    override fun javascript() = ""

    override suspend fun enterFullscreen(webView: WebpageAdapterWebView) {
    }

}