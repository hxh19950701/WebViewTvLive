package com.vasthread.webviewtv.playlist

data class ChannelGroup(
    var name: String,
    val channels: MutableList<Channel> = mutableListOf()
)