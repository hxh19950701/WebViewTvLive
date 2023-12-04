package com.hxh19950701.webviewtvlive.playlist

data class Channel(
    val name: String,
    val url: String,
    val x: Float = -1F,
    val y: Float = -1F,
    var groupName: String = "",
)