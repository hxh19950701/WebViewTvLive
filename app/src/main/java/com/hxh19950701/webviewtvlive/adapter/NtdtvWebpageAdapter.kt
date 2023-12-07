package com.hxh19950701.webviewtvlive.adapter

import android.view.KeyEvent
import com.hxh19950701.webviewtvlive.widget.WebpageAdapterWebView

class NtdtvWebpageAdapter : CommonWebpageAdapter() {

    override fun isBlockNetworkImage() = true

    override fun isAdaptedUrl(url: String) = url.contains("ntdtv.com.tw")

    override suspend fun enterFullscreen(webView: WebpageAdapterWebView) {
        enterFullscreenByPressKey(webView, KeyEvent.KEYCODE_F)
    }
}