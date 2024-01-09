package com.vasthread.webviewtv.adapter

class NtdtvWebpageAdapter : CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("ntdtv.com")

//    override fun getFullscreenElementId(): String {
//        return "#livestream_player_html5_api"
//    }

}