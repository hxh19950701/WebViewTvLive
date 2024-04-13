package com.vasthread.webviewtv

import com.vasthread.webviewtv.playlist.Channel
import org.junit.Test

class ChannelListJsonGenerator {

    @Test
    fun makeAll() {
        makeDefault();
        makeFull()
        makeCCTVAndHunanTV()
    }

    private fun makeDefault() {
        val channels = mutableListOf<Channel>()
            .addChannels("默认", fullChannels.extractGroup("央视")[0])
        savePlaylist("default_playlist", channels, "src/main/assets")
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