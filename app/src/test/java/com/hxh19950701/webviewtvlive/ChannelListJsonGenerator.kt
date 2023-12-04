package com.hxh19950701.webviewtvlive

import org.junit.Test

class ChannelListJsonGenerator {

    @Test
    fun makeCCTVAndHunanTV() {
        val title = "央视&湖南"
        val channels = mutableListOf<Channel>()
            .addChannels(title, *fullChannels.extractGroup("央视"))
            .addChannels(title, *fullChannels.extractGroup("湖南"))
        val playlist = Playlist.createFromAllChannels(title, channels)
        savePlaylist(playlist)
    }
}