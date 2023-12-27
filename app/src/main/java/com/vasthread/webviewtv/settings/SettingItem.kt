package com.vasthread.webviewtv.settings

@Suppress("ArrayInDataClass")
data class SettingItem(
    val title: String,
    val items: Array<String>? = null,
    var selectedItemPosition: Int = 0,
    val onClick: (() -> Unit)? = null,
    val onItemSelect: ((Int) -> Unit)? = null
)