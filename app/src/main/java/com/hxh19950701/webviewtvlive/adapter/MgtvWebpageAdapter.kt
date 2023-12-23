package com.hxh19950701.webviewtvlive.adapter

import android.view.KeyEvent
import com.hxh19950701.webviewtvlive.widget.WebpageAdapterWebView

class  MgtvWebpageAdapter : CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("live.mgtv.com")

    override suspend fun enterFullscreen(webView: WebpageAdapterWebView) {
        enterFullscreenByPressKey(webView, KeyEvent.KEYCODE_F)
    }
}