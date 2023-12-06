package com.hxh19950701.webviewtvlive.adapter

import android.view.KeyEvent

class NtdtvWebpageAdapter : CommonWebpageAdapter() {

    override fun isAdaptedUrl(url: String) = url.contains("ntdtv.com.tw")

    override suspend fun enterFullscreen(player: IPlayer) {
        enterFullscreenByPressKey(player, KeyEvent.KEYCODE_F)
    }
}