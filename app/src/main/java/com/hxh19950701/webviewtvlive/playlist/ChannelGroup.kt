package com.hxh19950701.webviewtvlive.playlist

data class ChannelGroup(
    var name: String,
    val channels: MutableList<Channel> = mutableListOf()
)