package com.vasthread.webviewtv.adapter

import android.view.KeyEvent
import com.vasthread.webviewtv.widget.WebpageAdapterWebView

class EbcWebpageAdapter : CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("news.ebc.net.tw")

    override fun javascript() = """
    var frame = document.getElementById('live-play-yt');
    if (frame) {
        var link = frame.src;
        var start = link.lastIndexOf('/');
        var end = link.lastIndexOf('?');
        var id = link.substring(start + 1, end);
        console.log("Youtube video id is " + id);
        var url = "https://m.youtube.com/watch?v=" + id;
        window.main.loadUrl(url);
    } else {
        console.log("No youtube player");
    }
    """.trimIndent()

    override suspend fun enterFullscreen(webView: WebpageAdapterWebView) {
        enterFullscreenByPressKey(webView, KeyEvent.KEYCODE_F)
    }
}