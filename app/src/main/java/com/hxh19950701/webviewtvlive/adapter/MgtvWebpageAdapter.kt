package com.hxh19950701.webviewtvlive.adapter

class MgtvWebpageAdapter : CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("live.mgtv.com")
}