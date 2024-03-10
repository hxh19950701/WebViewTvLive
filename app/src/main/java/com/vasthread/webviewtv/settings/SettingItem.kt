package com.vasthread.webviewtv.settings

import androidx.annotation.StringRes

@Suppress("ArrayInDataClass")
data class SettingItem(
    @StringRes val titleRes: Int,
    val items: Array<String>? = null,
    var selectedItemPosition: Int = 0,
    val onClick: (() -> Unit)? = null,
    val onItemSelect: ((Int) -> Unit)? = null
)