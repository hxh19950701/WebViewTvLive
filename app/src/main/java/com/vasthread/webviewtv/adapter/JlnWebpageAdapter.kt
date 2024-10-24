package com.vasthread.webviewtv.adapter

import android.view.KeyEvent
import com.vasthread.webviewtv.widget.WebpageAdapterWebView

class JlnWebpageAdapter: CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("jlntv.cn")

    override fun userAgent() = PC_USER_AGENT

    override fun javascript(): String {
        return """
            var btn = document.getElementsByClassName("vjs-big-play-button")[0];
            if (btn) {
                btn.click();
            }
        """.trimIndent() + super.javascript()
    }

    override fun getFullscreenElementId(): String {
        return "#live-player"
    }

    override fun isPlayingCheckEnabled(): Boolean {
        return false
    }

    override suspend fun enterFullscreen(webView: WebpageAdapterWebView) {
        enterFullscreenByPressKey(webView, KeyEvent.KEYCODE_F)
    }

}