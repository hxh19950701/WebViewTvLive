package com.hxh19950701.webviewtvlive

import com.hxh19950701.webviewtvlive.playlist.Channel
import com.hxh19950701.webviewtvlive.playlist.Playlist
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

    @Test
    fun makeFull() {
        val title = "完整"
        val playlist = Playlist.createFromAllChannels(title, fullChannels)
        savePlaylist(playlist)
    }
}