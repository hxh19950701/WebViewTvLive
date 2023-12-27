package com.vasthread.webviewtv.adapter

import android.view.KeyEvent
import com.vasthread.webviewtv.widget.WebpageAdapterWebView

class VoaNewsWebpageAdapter:CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("voanews.com")

    override suspend fun enterFullscreen(webView: WebpageAdapterWebView) {
        enterFullscreenByPressKey(webView, KeyEvent.KEYCODE_F)
    }

}