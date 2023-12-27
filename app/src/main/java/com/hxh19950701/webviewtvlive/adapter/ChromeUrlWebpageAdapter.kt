package com.hxh19950701.webviewtvlive.adapter

import com.hxh19950701.webviewtvlive.widget.WebpageAdapterWebView

class ChromeUrlWebpageAdapter: WebpageAdapter() {
    override fun isAdaptedUrl(url: String) = url.startsWith("chrome:") || url.startsWith("about:")

    override fun javascript() = ""

    override suspend fun enterFullscreen(webView: WebpageAdapterWebView) {
    }

}