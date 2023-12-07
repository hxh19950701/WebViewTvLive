package com.hxh19950701.webviewtvlive.adapter

object WebpageAdapterManager {

    private val supportedWebpageAdapters = mutableListOf<WebpageAdapter>(
        MgtvWebpageAdapter(),
        CctvWebpageAdapter(),
        GdtvWebpageAdapter(),
        NtdtvWebpageAdapter(),
        EbcWebpageAdapter(),
        CommonWebpageAdapter(),
    )

    fun get(url: String): WebpageAdapter {
        for (adapter in supportedWebpageAdapters) {
            if (adapter.isAdaptedUrl(url)) return adapter
        }
        return supportedWebpageAdapters[supportedWebpageAdapters.lastIndex]
    }
}