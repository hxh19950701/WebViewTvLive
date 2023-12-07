package com.hxh19950701.webviewtvlive.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.JavascriptInterface
import com.hxh19950701.webviewtvlive.adapter.WebpageAdapterManager
import com.tencent.smtt.export.external.interfaces.ConsoleMessage
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("SetJavaScriptEnabled")
class WebpageAdapterWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "WebpageAdapterWebView"
        private const val URL_CHROME = "chrome://"
        private const val URL_BLANK = "chrome://blank"
    }

    private var isInFullScreen = false

    var onPageFinished: ((String)->Unit)? = null
    var onProgressChanged: ((Int)->Unit)? = null
    var onFullscreenStateChanged: ((Boolean)->Unit)? = null

    private val client = object : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            //view.loadUrl(url)
            return true
        }

        override fun shouldOverrideKeyEvent(view: WebView, event: KeyEvent): Boolean {
            //Log.i(TAG, "shouldOverrideKeyEvent $event")
            return super.shouldOverrideKeyEvent(view, event)
        }

        override fun onUnhandledKeyEvent(view: WebView, event: KeyEvent) {
            //Log.i(TAG, "onUnhandledKeyEvent $event")
            super.onUnhandledKeyEvent(view, event)
        }

        override fun onLoadResource(view: WebView, url: String) {
            //Log.i(TAG, "onLoadResource, $url")
            super.onLoadResource(view, url)
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            Log.i(TAG, "onPageStarted, $url")
            super.onPageStarted(view, url, favicon)
        }

        @Suppress("DEPRECATION")
        override fun onPageFinished(view: WebView, url: String) {
            Log.i(TAG, "onPageFinished, $url")
            super.onPageFinished(view, url)
            while (view.canZoomOut()) view.zoomOut()
            view.evaluateJavascript(WebpageAdapterManager.get(url).javascript()) {}
            onPageFinished?.invoke(url)
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
            Log.i(TAG, "onProgressChanged, ${view.url} $progress")
            super.onProgressChanged(view, progress)
            onProgressChanged?.invoke(progress)
        }

        override fun onConsoleMessage(msg: ConsoleMessage): Boolean {
            Log.i("ConsoleMessage", msg.message())
            return true
        }

        override fun onShowCustomView(view: View, callback: IX5WebChromeClient.CustomViewCallback) {
            this.view = view
            this.callback = callback
            addView(view)
            isInFullScreen = true
            onFullscreenStateChanged?.invoke(true)
        }

        override fun onHideCustomView() {
            removeView(view)
            callback?.onCustomViewHidden()
            view = null
            callback = null
            isInFullScreen = false
            onFullscreenStateChanged?.invoke(false)
        }
    }

    init {
        settings.apply {
            @Suppress("DEPRECATION")
            javaScriptEnabled = true
            domStorageEnabled = true
            //cacheMode = WebSettings.LOAD_NO_CACHE
            mediaPlaybackRequiresUserGesture = false

            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportZoom(true)
        }
        apply {
            webViewClient = client
            webChromeClient = chromeClient
            setBackgroundColor(Color.BLACK)
            addJavascriptInterface(this, "main")
        }
    }
    @JavascriptInterface
    override fun loadUrl(url: String) {
        stopLoading()
        settings.userAgentString = WebpageAdapterManager.get(url).userAgent()
        super.loadUrl(url)
    }

    fun isInFullscreen() = isInFullScreen

    @Suppress("unused")
    @JavascriptInterface
    fun schemeEnterFullscreen() {
        Log.i(TAG, "schemeEnterFullscreen")
        CoroutineScope(Dispatchers.Main).launch {
            //lastJob?.apply { cancelAndJoin() }
            //webpageAdapter!!.enterFullscreen(this@ChannelPlayerView)
            WebpageAdapterManager.get(url).enterFullscreen(this@WebpageAdapterWebView)
        }
    }
}