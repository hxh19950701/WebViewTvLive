package com.hxh19950701.webviewtvlive.adapter

import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

open class CommonWebpageAdapter : WebpageAdapter() {

    companion object {
        const val PC_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36"

        internal val COMMON_JAVASCRIPT = """javascript:
            var v = document.getElementsByTagName('video')[0];
            if (v == null) {
                console.log("No video tag found.");
                window.main.schemeEnterFullscreen();
            } else {
                v.addEventListener('timeupdate', function() { v.volume = 1 });
                console.log("v.paused: " + v.paused)
                if (v.paused) {
                     v.addEventListener('canplay', function(e) { window.main.schemeEnterFullscreen() });
                } else {
                     window.main.schemeEnterFullscreen();
                }
            }
        """.trimIndent()

        internal const val ENTER_FULLSCREEN_DELAY = 2000L
        internal const val CLICK_DURATION = 50L
        internal const val DOUBLE_CLICK_INTERVAL = 50L
        internal const val ENTER_FULLSCREEN_MAX_TRY = 5
    }

    override fun userAgent(): String? = PC_USER_AGENT

    override fun isAdaptedUrl(url: String) = true

    override fun javascript() = COMMON_JAVASCRIPT

    override suspend fun enterFullscreen(player: IPlayer) {
        enterFullscreenByDoubleScreenClick(player)
    }

    protected suspend fun enterFullscreenByDoubleScreenClick(player: IPlayer, xPos: Float = 0.4F, yPos: Float = 0.6F) {
        delay(ENTER_FULLSCREEN_DELAY)
        var times = 0
        val size = player.getScreenSize()
        val x = size.x * xPos
        val y = size.y * yPos
        while (!player.isInFullscreen() && times < ENTER_FULLSCREEN_MAX_TRY) {
            Log.i(TAG, "enterFullscreen trying for ${++times} times")
            var canceled = false
            val canceledCallback = { canceled = true }
            screenClick(player, x, y, canceledCallback)
            delayBy(DOUBLE_CLICK_INTERVAL, canceledCallback)
            screenClick(player, x, y, canceledCallback)
            if (canceled) break
            delayBy(ENTER_FULLSCREEN_DELAY, canceledCallback)
            if (canceled) break
        }
    }

    private suspend fun screenClick(player: IPlayer, x: Float, y: Float, canceledCallback: () -> Unit) {
        val downTime = SystemClock.uptimeMillis()
        player.sendMotionEvent(MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0))
        delayBy(CLICK_DURATION, canceledCallback)
        player.sendMotionEvent(MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0))
    }

    private suspend fun keyClick(player: IPlayer, code: Int, canceledCallback: () -> Unit) {
        val downTime = SystemClock.uptimeMillis()
        player.sendKeyEvent(KeyEvent(downTime, downTime, KeyEvent.ACTION_DOWN, code, 0))
        delayBy(CLICK_DURATION, canceledCallback)
        player.sendKeyEvent(KeyEvent(downTime, SystemClock.uptimeMillis(), KeyEvent.ACTION_UP, code, 0))
    }

    private suspend fun delayBy(mills: Long, canceledCallback: () -> Unit) {
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
}