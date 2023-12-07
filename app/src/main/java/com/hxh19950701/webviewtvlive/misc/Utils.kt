package com.hxh19950701.webviewtvlive.misc

import android.os.SystemClock
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

suspend fun delayBy(mills: Long, canceledCallback: () -> Unit) {
    val start = SystemClock.uptimeMillis()
    var duration = mills
    var canceled = false
    do {
        try {
            delay(duration)
        } catch (_: CancellationException) {
            canceled = true
        }
        duration = start + mills - SystemClock.uptimeMillis()
    } while (duration > 0)

    if (canceled) {
        canceledCallback()
    }
}