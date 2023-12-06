package com.hxh19950701.webviewtvlive.adapter

import android.view.KeyEvent

class GdtvWebpageAdapter : CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("m.gdtv.cn")

//    override fun javascript() = """javascript:
//    var videoPlayerDiv = document.querySelector('.video-player');
//      if (videoPlayerDiv) {
//        videoPlayerDiv.style.width = '100%';
//        videoPlayerDiv.style.height = '100%';
//        videoPlayerDiv.style.position = 'absolute';
//        videoPlayerDiv.style.top = '0';
//        videoPlayerDiv.style.left = '0';
//        videoPlayerDiv.style.zIndex = '9999';
//      }
//    """.trimIndent()

    override fun userAgent() = null

    override suspend fun enterFullscreen(player: IPlayer) {
        enterFullscreenByPressKey(player, KeyEvent.KEYCODE_F)
    }
}