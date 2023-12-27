package com.vasthread.webviewtv.adapter

import com.vasthread.webviewtv.widget.WebpageAdapterWebView

class TbsDebugWebpageAdapter : WebpageAdapter() {
    override fun isAdaptedUrl(url: String): Boolean {
        return url.contains("debugtbs.qq.com") || url.contains("res.imtt.qq.com")
    }

    override fun javascript(): String {
        return ""
    }

    override suspend fun enterFullscreen(webView: WebpageAdapterWebView) {
    }
}