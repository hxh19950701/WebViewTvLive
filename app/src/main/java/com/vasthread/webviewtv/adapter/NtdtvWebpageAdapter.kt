package com.vasthread.webviewtv.adapter

class NtdtvWebpageAdapter : CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("ntdtv.com")

}