package com.vasthread.webviewtv.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.webkit.JavascriptInterface
import android.widget.FrameLayout
import com.tencent.smtt.export.external.extension.interfaces.IX5WebSettingsExtension
import com.tencent.smtt.export.external.extension.proxy.ProxyWebChromeClientExtension
import com.tencent.smtt.export.external.extension.proxy.ProxyWebViewClientExtension
import com.tencent.smtt.export.external.interfaces.ConsoleMessage
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.export.external.interfaces.JsResult
import com.tencent.smtt.export.external.interfaces.MediaAccessPermissionsCallback
import com.tencent.smtt.export.external.interfaces.PermissionRequest
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.vasthread.webviewtv.adapter.WebpageAdapterManager
import com.vasthread.webviewtv.misc.isMainThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.commons.lang3.math.Fraction
import kotlin.system.measureTimeMillis

@Suppress("unused", "DEPRECATION")
@SuppressLint("SetJavaScriptEnabled")
class WebpageAdapterWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "WebpageAdapterWebView"
        const val URL_BLANK = "about:blank"
        private const val SHOW_WAITING_VIEW_DELAY = 3000L
        private const val MAX_ZOOM_OUT_LEVEL = 3
        private const val CHECK_PAGE_LOADING_INTERVAL = 50L
    }

    private var requestedUrl = ""
    private var isInFullscreen = false
    private var isPageLoading = false
    private val showWaitingViewAction = Runnable { onWaitingStateChanged?.invoke(true) }
    private val dismissWaitingViewAction = Runnable { onWaitingStateChanged?.invoke(false) }

    lateinit var fullscreenContainer: FrameLayout
    var onWaitingStateChanged: ((Boolean) -> Unit)? = null
    var onPageFinished: ((String) -> Unit)? = null
    var onProgressChanged: ((Int) -> Unit)? = null
    var onFullscreenStateChanged: ((Boolean) -> Unit)? = null

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

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            handler.proceed()
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            Log.i(TAG, "onPageStarted, $url")
            isPageLoading = true
            disablePlayCheck()
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            Log.i(TAG, "onPageFinished, $url")
            isPageLoading = false
        }

        override fun onReceivedHttpError(view: WebView, request: WebResourceRequest, response: WebResourceResponse) {
            super.onReceivedHttpError(view, request, response)
            Log.i(TAG, "Http error: ${response.statusCode} ${request.url}")
        }

    }

    private val clientExtension = object : ProxyWebViewClientExtension() {

    }

    private val chromeClient = object : WebChromeClient() {

        private var view: View? = null
        private var callback: IX5WebChromeClient.CustomViewCallback? = null
        private var lastUrl = ""
        private var lastProgress = 0

        fun markNewPage() {
            lastUrl = ""
        }

        override fun onJsAlert(view: WebView, url: String, message: String?, result: JsResult): Boolean {
            result.cancel()
            return true
        }

        override fun onProgressChanged(view: WebView, progress: Int) {
            super.onProgressChanged(view, progress)
            if (view.url != lastUrl || progress > lastProgress) {
                Log.i(TAG, "$url, progress=$progress")
                onProgressChanged?.invoke(progress)
                disablePlayCheck()
                adjustWideViewPort()
                if (progress == 100) {
                    evaluateJavascript(WebpageAdapterManager.get(url).javascript(), null)
                    onPageFinished?.invoke(url)
                }
                lastUrl = view.url
                lastProgress = progress
            }
        }

        override fun onConsoleMessage(msg: ConsoleMessage): Boolean {
            when (msg.messageLevel()) {
                ConsoleMessage.MessageLevel.DEBUG, null -> Log.d(TAG, msg.message())
                ConsoleMessage.MessageLevel.LOG, ConsoleMessage.MessageLevel.TIP -> Log.i(TAG, msg.message())
                ConsoleMessage.MessageLevel.WARNING -> Log.w(TAG, msg.message())
                ConsoleMessage.MessageLevel.ERROR -> Log.e(TAG, msg.message())
            }
            return true
        }

        @Suppress("RemoveRedundantQualifierName")
        fun generateLayoutParams(): FrameLayout.LayoutParams {
            val videoRatio = Fraction.getFraction(16, 9)
            val screenRadio = Fraction.getFraction(fullscreenContainer.width, fullscreenContainer.height)
            val compare = screenRadio.compareTo(videoRatio)
            return if (compare == 0) FrameLayout.LayoutParams(screenRadio.numerator, screenRadio.denominator, Gravity.CENTER)
            else if (compare > 0) FrameLayout.LayoutParams(screenRadio.denominator * videoRatio.numerator / videoRatio.denominator, screenRadio.denominator, Gravity.CENTER)
            else FrameLayout.LayoutParams(screenRadio.numerator, screenRadio.numerator * videoRatio.denominator / videoRatio.numerator, Gravity.CENTER)
        }

        override fun onShowCustomView(view: View, callback: IX5WebChromeClient.CustomViewCallback) {
            Log.i(TAG, "onShowCustomView")
            this.view = view
            this.callback = callback
            fullscreenContainer.addView(view, generateLayoutParams())
            isInFullscreen = true
            onFullscreenStateChanged?.invoke(true)
        }

        override fun onHideCustomView() {
            Log.i(TAG, "onHideCustomView")
            fullscreenContainer.removeView(view)
            callback?.onCustomViewHidden()
            view = null
            callback = null
            isInFullscreen = false
            onFullscreenStateChanged?.invoke(false)
        }

        override fun onPermissionRequest(request: PermissionRequest) {
            super.onPermissionRequest(request)
        }
    }

    private val chromeClientExtension = object : ProxyWebChromeClientExtension() {

        override fun jsRequestFullScreen() {
            Log.i(TAG, "jsRequestFullScreen")
        }

        override fun h5videoRequestFullScreen(s: String) {
            Log.i(TAG, "h5videoRequestFullScreen")
        }

        override fun onPermissionRequest(origin: String, resources: Long, callback: MediaAccessPermissionsCallback): Boolean {
            Log.i(TAG, "onPermissionRequest, origin=$origin, resources=$resources")
            callback.invoke(origin, 0, true)
            return true
        }
    }

    init {
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            useWideViewPort = true
            loadWithOverviewMode = true
            setAppCacheEnabled(true)
            cacheMode = WebSettings.LOAD_DEFAULT
        }
        apply {
            webViewClient = client
            webViewClientExtension = clientExtension
            webChromeClient = chromeClient
            webChromeClientExtension = chromeClientExtension
            setBackgroundColor(Color.BLACK)
            addJavascriptInterface(this, "main")
        }
    }

    @JavascriptInterface
    override fun loadUrl(url: String) {
        requestedUrl = url
        CoroutineScope(Dispatchers.Main).launch {
            waitCurrentPageFinish(url)
            if (requestedUrl == url) {
                settings.apply {
                    loadsImagesAutomatically = false
                    blockNetworkImage = true
                    userAgentString = WebpageAdapterManager.get(url).userAgent()
                }
                settingsExtension?.apply {
                    setPicModel(IX5WebSettingsExtension.PicModel_NoPic)
                }
                disablePlayCheck()
                Log.i(TAG, "Load url $url")
                super.loadUrl(url)
            } else {
                Log.i(TAG, "New url requested, ignore.")
            }
        }
    }

    private suspend fun waitCurrentPageFinish(url: String) {
        if (isPageLoading) {
            Log.i(TAG, "Wait current page finish.")
            val cost = measureTimeMillis {
                stopLoading()
                while (isPageLoading && requestedUrl == url) {
                    delay(CHECK_PAGE_LOADING_INTERVAL)
                }
            }
            Log.i(TAG, "Done waiting, cost ${cost}ms.")
        }
    }

    override fun reload() {
        chromeClient.markNewPage()
        super.reload()
    }

    override fun stopLoading() {
        super.stopLoading()
        disablePlayCheck()
    }

    fun isInFullscreen() = isInFullscreen

    @JavascriptInterface
    fun schemeEnterFullscreen() {
        if (isInFullscreen()) return
        Log.i(TAG, "schemeEnterFullscreen")
        CoroutineScope(Dispatchers.Main).launch {
            WebpageAdapterManager.get(url).tryEnterFullscreen(this@WebpageAdapterWebView)
        }
    }

    @JavascriptInterface
    fun notifyVideoPlaying() {
        disablePlayCheck()
        enablePlayCheck()
    }

    @JavascriptInterface
    fun enablePlayCheck() {
        postDelayed(showWaitingViewAction, SHOW_WAITING_VIEW_DELAY)
    }

    @JavascriptInterface
    fun disablePlayCheck() {
        removeCallbacks(showWaitingViewAction)
        dismissWaitingViewAction.let { if (isMainThread()) it.run() else post(it) }
    }

    private fun adjustWideViewPort() {
        var level = 0
        while (canZoomOut() && level < 3) {
            zoomOut()
            ++level
        }
    }
}