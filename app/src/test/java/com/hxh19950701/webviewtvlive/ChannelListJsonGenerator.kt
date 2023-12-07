package com.hxh19950701.webviewtvlive

import com.hxh19950701.webviewtvlive.playlist.Channel
import org.junit.Test

class ChannelListJsonGenerator {

    @Test
    fun makeAll() {
        makeFull()
        makeCCTVAndHunanTV()
    }

    @Test
    fun makeCCTVAndHunanTV() {
        val title = "央视&湖南"
        val channels = mutableListOf<Channel>()
            .addChannels(title, *fullChannels.extractGroup("央视"))
            .addChannels(title, *fullChannels.extractGroup("湖南"))
        savePlaylist(title, channels)
    }

    @Test
    fun makeFull() {
        savePlaylist("完整", fullChannels)
    }
}