package com.hxh19950701.webviewtvlive.adapter

import android.view.KeyEvent
import com.hxh19950701.webviewtvlive.widget.WebpageAdapterWebView

class EbcWebpageAdapter : CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("news.ebc.net.tw")

    override fun javascript() = """
    var frame = document.getElementById('live-play-yt');
    if (frame) {
        var link = frame.src;
        var start = link.lastIndexOf('/');
        var end = link.lastIndexOf('?');
        var id = link.substring(start + 1, end);
        console.log(id);
        var url = "http://www.youtube.com/watch?v=" + id;
        window.location.replace(url);
    } else {
    """.trimIndent() + super.javascript() + "}"

    override suspend fun enterFullscreen(webView: WebpageAdapterWebView) {
        enterFullscreenByPressKey(webView, KeyEvent.KEYCODE_F)
    }
}