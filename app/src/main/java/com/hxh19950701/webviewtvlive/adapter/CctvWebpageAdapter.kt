package com.hxh19950701.webviewtvlive.adapter

class CctvWebpageAdapter : CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("tv.cctv.com")
}