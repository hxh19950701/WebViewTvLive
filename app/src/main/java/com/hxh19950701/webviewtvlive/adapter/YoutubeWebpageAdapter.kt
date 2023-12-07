package com.hxh19950701.webviewtvlive.adapter

class YoutubeWebpageAdapter : CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("youtube.com")

    override fun userAgent() = null
}