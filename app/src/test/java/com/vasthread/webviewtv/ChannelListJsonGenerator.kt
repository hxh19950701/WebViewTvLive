package com.vasthread.webviewtv

import com.vasthread.webviewtv.playlist.Channel
import org.junit.Test

class ChannelListJsonGenerator {

    @Test
    fun makeAll() {
        makeDefault();
        makeFull()
        makeCCTVAndHunanTV()
        makeCCTVAndSatelliteTV()
        makeChinaMainland()
    }

    private fun makeDefault() {
        val channels = mutableListOf<Channel>()
            .addChannels("央视", fullChannels.extractGroup("央视"))
        savePlaylist("default_playlist", channels, "src/main/assets")
    }

    @Test
    fun makeCCTVAndHunanTV() {
        val title = "cctv_and_hunan_tv_single_list"
        val channels = mutableListOf<Channel>()
            .addChannels("央视&湖南", fullChannels.extractGroup("央视"))
            .addChannels("央视&湖南", fullChannels.extractGroup("湖南"))
        savePlaylist(title, channels)
    }

    @Test
    fun makeCCTVAndSatelliteTV() {
        val title = "cctv_and_satellite_tv_single_list"
        val channels = mutableListOf<Channel>()
            .addChannels("央视&卫视", fullChannels.extractGroup("央视"))
            .addChannels("央视&卫视", fullChannels.extractGroup("卫视"))
        savePlaylist(title, channels)
    }

    @Test
    fun makeChinaMainland() {
        val title = "china_mainland"
        val channels = fullChannels
            .extractAllGroupsExclude("香港")
            .extractAllGroupsExclude("澳門")
            .extractAllGroupsExclude("台灣")
            .extractAllGroupsExclude("海外")
        savePlaylist(title, channels)
    }

    @Test
    fun makeFull() {
        savePlaylist("full", fullChannels)
    }
}