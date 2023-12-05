package com.hxh19950701.webviewtvlive.widget

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.webkit.JavascriptInterface
import android.widget.FrameLayout
import com.hxh19950701.webviewtvlive.playlist.Channel
import com.hxh19950701.webviewtvlive.R
import com.hxh19950701.webviewtvlive.delayBy
import com.hxh19950701.webviewtvlive.settings.SettingsManager
import com.tencent.smtt.export.external.interfaces.ConsoleMessage
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChannelPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "ChannelPlayerView"
        const val UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36"
        const val JS = """javascript:
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
//             document.onkeyup = function(e) {
//              console.log(e.key)
//             }
"""
        const val ENTER_FULLSCREEN_DELAY = 500L
        const val CLICK_DURATION = 50L
        const val DOUBLE_CLICK_INTERVAL = 50L
        const val URL_BLANK = "chrome://blank"
    }

    private val webView: WebView
    private lateinit var channelBarView: ChannelBarView
    private var isInFullScreen: Boolean = false
    var activity: Activity? = null
    var channel: Channel? = null
        set(value) {
            if (field == value) return
            field = value
            //webView.loadUrl(URL_BLANK)
            value?.apply {
                webView.loadUrl(url)
                channelBarView.setCurrentChannelAndShow(value)
            }
        }
    var dismissAllViewCallback: (() -> Unit)? = null

    private val client = object : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun shouldOverrideKeyEvent(p0: WebView, p1: KeyEvent): Boolean {
            return super.shouldOverrideKeyEvent(p0, p1)
        }

        override fun onUnhandledKeyEvent(p0: WebView, p1: KeyEvent) {
            super.onUnhandledKeyEvent(p0, p1)
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            //if (url.startsWith("chrome://")) return
            while (view.canZoomOut()) view.zoomOut()
            view.evaluateJavascript(JS) {}
            channelBarView.dismiss();
            Log.i(TAG, "Load complete, $url")
        }

        override fun onReceivedHttpError(view: WebView, request: WebResourceRequest, response: WebResourceResponse) {
            super.onReceivedHttpError(view, request, response)
            Log.i(TAG, "Http error: ${response.statusCode}")
        }

    }

    private val chromeClient = object : WebChromeClient() {

        private var view: View? = null
        private var callback: IX5WebChromeClient.CustomViewCallback? = null

        override fun onProgressChanged(view: WebView, progress: Int) {
            super.onProgressChanged(view, progress)
            channelBarView.setProgress(progress)
        }

        override fun onConsoleMessage(msg: ConsoleMessage): Boolean {
            Log.i(TAG, msg.message())
            return true
        }

        override fun onShowCustomView(view: View, callback: IX5WebChromeClient.CustomViewCallback) {
            this.view = view
            this.callback = callback
            addView(view)
            activity?.apply { window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN }
            isInFullScreen = true
        }

        override fun onHideCustomView() {
            removeView(view)
            callback?.onCustomViewHidden()
            activity?.apply { window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN }
            isInFullScreen = false
        }
    }

    private val javascriptInterface = object {

        private var currentJob: Job? = null

        @Suppress("unused")
        @JavascriptInterface
        fun schemeEnterFullscreen() {
            Log.i(TAG, "schemeEnterFullscreen")
            val lastJob = currentJob
            currentJob = CoroutineScope(Dispatchers.Main).launch {
                lastJob?.apply { cancelAndJoin() }
                enterFullScreen()
            }
        }

        @Suppress("unused")
        @JavascriptInterface
        fun log(s: String) {
            Log.i(TAG, s)
        }
    }

    private val gestureDetector = GestureDetector(context, object : GestureDetector.OnGestureListener {

        override fun onDown(e: MotionEvent) = true

        override fun onShowPress(e: MotionEvent) = Unit

        override fun onSingleTapUp(e: MotionEvent) = performClick()

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float) = false

        override fun onLongPress(e: MotionEvent) = Unit

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float) = false

    })

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_channel_player, this)
        webView = findViewById(R.id.webView)
        channelBarView = findViewById(R.id.channelBarView)
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            mediaPlaybackRequiresUserGesture = false

            useWideViewPort = true
            loadWithOverviewMode = true
            userAgentString = UA
            setSupportZoom(true)
        }
        webView.apply {
            webViewClient = client
            webChromeClient = chromeClient
            setBackgroundColor(Color.BLACK)
            addJavascriptInterface(javascriptInterface, "main")
        }
    }


    private suspend fun enterFullScreen() {
        delay(ENTER_FULLSCREEN_DELAY)
        var times = 0
        val x = if (channel!!.x >= 0) channel!!.x else width * 0.4F
        val y = if (channel!!.y >= 0) channel!!.y else height * 0.6F
        while (!isInFullScreen) {
            Log.i(TAG, "enterFullscreen trying for ${++times} times")
            var canceled = false
            val canceledCallback = { canceled = true }
            click(x, y, canceledCallback)
            delayBy(DOUBLE_CLICK_INTERVAL, canceledCallback)
            click(x, y, canceledCallback)
            if (canceled) break
            delayBy(ENTER_FULLSCREEN_DELAY, canceledCallback)
            if (canceled) break
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return if (SettingsManager.isWebViewTouchable()) {
            dismissAllViewCallback?.invoke()
            super.dispatchTouchEvent(ev)
        } else {
            gestureDetector.onTouchEvent(ev)
        }
    }

    override fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
        return false
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return false
    }

    private suspend fun click(x: Float, y: Float, canceledCallback: () -> Unit) {
        val downTime = SystemClock.uptimeMillis()
        super.dispatchTouchEvent(MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0))
        delayBy(CLICK_DURATION, canceledCallback)
        super.dispatchTouchEvent(MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0))
    }
}