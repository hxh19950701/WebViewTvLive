package com.hxh19950701.webviewtvlive.adapter

import com.hxh19950701.webviewtvlive.widget.WebpageAdapterWebView

class SztvWebpageAdapter : CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("sztv.com.cn")

    override suspend fun enterFullscreen(webView: WebpageAdapterWebView) {
        enterFullscreenByPressKey(webView)
    }
}