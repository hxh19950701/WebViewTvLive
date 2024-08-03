package com.vasthread.webviewtv.misc

import android.net.TrafficStats
import android.os.Looper

private val mainLooper = Looper.getMainLooper()

fun adjustValue(value: Int, size: Int, next: Boolean): Int {
    return if (next) {
        if (value + 1 >= size) 0 else value + 1
    } else {
        if (value - 1 < 0) size - 1 else value - 1
    }
}

fun isMainThread() = Looper.myLooper() == mainLooper

private val myUid = application.applicationInfo.uid

fun getTrafficBytes(): Long {
    return TrafficStats.getUidRxBytes(myUid) + TrafficStats.getUidTxBytes(myUid)
}