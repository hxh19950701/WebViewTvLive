package com.vasthread.webviewtv.adapter

class TdmWebpageAdapter : CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("tdm.com.mo")

    override fun getEnterFullscreenButtonElementId() = "button.mejs__button"

}