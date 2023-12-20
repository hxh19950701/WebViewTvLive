package com.hxh19950701.webviewtvlive.adapter

class NtdtvWebpageAdapter : CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("ntdtv.com")

}