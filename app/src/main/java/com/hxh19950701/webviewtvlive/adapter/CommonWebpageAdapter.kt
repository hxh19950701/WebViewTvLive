package com.hxh19950701.webviewtvlive.adapter

import android.graphics.Point
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import com.hxh19950701.webviewtvlive.misc.application
import com.hxh19950701.webviewtvlive.widget.WebpageAdapterWebView
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import java.io.BufferedReader

open class CommonWebpageAdapter : WebpageAdapter() {

    companion object {
        internal const val PC_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        internal const val ENTER_FULLSCREEN_DELAY = 2000L
        internal const val CLICK_DURATION = 50L
        internal const val DOUBLE_CLICK_INTERVAL = 50L
        internal const val ENTER_FULLSCREEN_MAX_TRY = 5
    }

    override fun userAgent(): String? = PC_USER_AGENT

    override fun isAdaptedUrl(url: String) = true

    override fun javascript(): String {
        return application.assets.open("default.js").bufferedReader().use(BufferedReader::readText)
    }

    override suspend fun enterFullscreen(webView: WebpageAdapterWebView) {
        enterFullscreenByDoubleScreenClick(webView)
    }

    protected suspend fun enterFullscreenByDoubleScreenClick(webView: WebpageAdapterWebView, xPos: Float = 0.4F, yPos: Float = 0.6F) {
        try {
            val url = webView.getRequestedUrl()
            checkCancellation(webView, url)
            var times = 0
            val size = Point(webView.width, webView.height)
            val x = size.x * xPos
            val y = size.y * yPos
            while (times < ENTER_FULLSCREEN_MAX_TRY) {
                delay(ENTER_FULLSCREEN_DELAY)
                checkCancellation(webView, url)
                screenClick(webView, x, y)
                checkCancellation(webView, url)
                delay(DOUBLE_CLICK_INTERVAL)
                checkCancellation(webView, url)
                screenClick(webView, x, y)
                Log.i(TAG, "enterFullscreen, tried ${++times} times")
            }
        } catch (e: Exception) {
            e.message?.let { Log.i(TAG, it) }
        }
    }

    protected suspend fun enterFullscreenByPressKey(webView: WebpageAdapterWebView, code: Int) {
        try {
            val url = webView.getRequestedUrl()
            checkCancellation(webView, url)
            var times = 0
            while (times < ENTER_FULLSCREEN_MAX_TRY) {
                delay(ENTER_FULLSCREEN_DELAY)
                checkCancellation(webView, url)
                keyClick(webView, code)
                Log.i(TAG, "enterFullscreen, tried ${++times} times")
            }
        } catch (e: Exception) {
            e.message?.let { Log.i(TAG, it) }
        }
    }

    private fun checkCancellation(webView: WebpageAdapterWebView, url: String) {
        if (webView.isInFullscreen()) {
            throw CancellationException("Already fullscreen, stop.")
        }
        if (webView.getRequestedUrl() != url) {
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