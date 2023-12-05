package com.hxh19950701.webviewtvlive

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

private var _application: Context? = null
val application: Context
    get() {
        return _application!!
    }

fun setApplication(application: Context) {
    _application = application
}

val preference: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(application) }
