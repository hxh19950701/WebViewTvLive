package com.vasthread.webviewtv.adapter

import android.view.KeyEvent
import com.vasthread.webviewtv.widget.WebpageAdapterWebView

class FgtvWebpageAdapter :CommonWebpageAdapter(){

    override fun isAdaptedUrl(url: String) = url.contains("4gtv")

    override suspend fun enterFullscreen(webView: WebpageAdapterWebView) {
        enterFullscreenByPressKey(webView, KeyEvent.KEYCODE_F)
    }
}