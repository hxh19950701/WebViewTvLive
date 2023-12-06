package com.hxh19950701.webviewtvlive.adapter

import android.view.KeyEvent

class EbcWebpageAdapter : CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("news.ebc.net.tw")

    override fun javascript() = """
    var video = document.getElementById('live-play-yt');
    if (video) {
        console.log("Video tag found.");
        video.addEventListener('timeupdate', function() { video.volume = 1 });
        document.onkeyup = function(e) {
        	console.log('Key up: ' + e.key);
        	if (e.key == 'f') { video.requestFullscreen() }
        }
    } else {
        console.log("No video tag found.");
    }
    """.trimIndent()

    override fun userAgent() = null

    override suspend fun enterFullscreen(player: IPlayer) {
        enterFullscreenByPressKey(player, KeyEvent.KEYCODE_F)
    }
}