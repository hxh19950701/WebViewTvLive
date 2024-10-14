package com.vasthread.webviewtv.adapter

import android.graphics.Point
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import com.vasthread.webviewtv.misc.application
import com.vasthread.webviewtv.widget.WebpageAdapterWebView
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import java.io.BufferedReader

open class CommonWebpageAdapter : WebpageAdapter() {

    companion object {
        internal const val PC_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36"
        internal const val ENTER_FULLSCREEN_DELAY = 1000L
        internal const val CLICK_DURATION = 50L
        internal const val DOUBLE_CLICK_INTERVAL = 50L
        internal const val CHECK_CANCELLATION_INTERVAL = 50L
        internal const val CHECK_FOCUS_INTERVAL = 500L
        internal const val ENTER_FULLSCREEN_MAX_TRY = 10
        private val JAVASCRIPT_TEMPLATE = application.assets.open("default_js_template.js").bufferedReader().use(BufferedReader::readText)
    }

    override fun userAgent(): String? = PC_USER_AGENT

    override fun isAdaptedUrl(url: String) = true

    open fun getFullscreenElementId() = "video"

    open fun getEnterFullscreenButtonElementId() = "%enter_fullscreen_button%"

    override fun javascript(): String {
        return JAVASCRIPT_TEMPLATE
            .replaceFirst("%selector%", getFullscreenElementId())
            .replaceFirst("%enter_fullscreen_button%", getEnterFullscreenButtonElementId())
            .replaceFirst("%playing_check_enabled%", isPlayingCheckEnabled().toString())
    }

    override suspend fun enterFullscreen(webView: WebpageAdapterWebView) {
        enterFullscreenByPressKey(webView, KeyEvent.KEYCODE_F)
    }

    protected suspend fun enterFullscreenByDoubleScreenClick(webView: WebpageAdapterWebView, xPos: Float = 0.4F, yPos: Float = 0.6F) {
        try {
            val url = webView.url
            checkCancellation(webView, url)
            var times = 0
            val size = Point(webView.width, webView.height)
            val x = size.x * xPos
            val y = size.y * yPos
            while (times < ENTER_FULLSCREEN_MAX_TRY) {
                delayAndCheckCancellation(webView, url, ENTER_FULLSCREEN_DELAY)
                screenClick(webView, x, y)
                delayAndCheckCancellation(webView, url, DOUBLE_CLICK_INTERVAL)
                screenClick(webView, x, y)
                Log.i(TAG, "enterFullscreenByDoubleScreenClick, x=$x, y=$y, times=${++times}")
            }
        } catch (e: Exception) {
            e.message?.let { Log.i(TAG, it) }
        }
    }

    protected suspend fun enterFullscreenByPressKey(webView: WebpageAdapterWebView, keycode: Int = KeyEvent.KEYCODE_F) {
        try {
            val url = webView.url
            checkCancellation(webView, url)
            var times = 0
            while (times < ENTER_FULLSCREEN_MAX_TRY) {
                delayAndCheckCancellation(webView, url, ENTER_FULLSCREEN_DELAY)
                while (!webView.hasFocus() && !webView.isInTouchMode) {
                    delayAndCheckCancellation(webView, url, CHECK_FOCUS_INTERVAL)
                }
                webView.requestFocus()
                keyClick(webView, keycode)
                Log.i(TAG, "enterFullscreenByPressKey, keycode=${KeyEvent.keyCodeToString(keycode)}, times=${++times}")
            }
        } catch (e: Exception) {
            e.message?.let { Log.i(TAG, it) }
        }
    }

    private suspend fun delayAndCheckCancellation(webView: WebpageAdapterWebView, url: String, timeMills: Long) {
        val start = SystemClock.uptimeMillis()
        var duration = timeMills
        do {
            delay(CHECK_CANCELLATION_INTERVAL)
            checkCancellation(webView, url)
            duration = start + timeMills - SystemClock.uptimeMillis()
        } while (duration > 0)
    }

    private fun checkCancellation(webView: WebpageAdapterWebView, url: String) {
        if (webView.isInFullscreen()) {
            throw CancellationException("Already fullscreen, stop.")
        }
        if (webView.url != url) {
            throw CancellationException("Url changed: $url -> ${webView.url}, stop.")
        }
    }

    private suspend fun screenClick(webView: WebpageAdapterWebView, x: Float, y: Float) {
        val downTime = SystemClock.uptimeMillis()
        webView.dispatchTouchEvent(MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0))
        delay(CLICK_DURATION)
        webView.dispatchTouchEvent(MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0))
    }

    private suspend fun keyClick(webView: WebpageAdapterWebView, code: Int) {
        val downTime = SystemClock.uptimeMillis()
        webView.dispatchKeyEvent(KeyEvent(downTime, downTime, KeyEvent.ACTION_DOWN, code, 0))
        delay(CLICK_DURATION)
        webView.dispatchKeyEvent(KeyEvent(downTime, SystemClock.uptimeMillis(), KeyEvent.ACTION_UP, code, 0))
    }

}