package com.vasthread.webviewtv.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.os.SystemClock
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

typealias LP = FrameLayout.LayoutParams

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
        private const val BLANK_PAGE_WAIT = 800L
        val RATIO_16_9 = Fraction.getFraction(16, 9)!!
        val RATIO_4_3 = Fraction.getFraction(4, 3)!!
    }

    private var requestedUrl = ""
    private val videoSize = Point()
    private var isInFullscreen = false
    private val loadingInfo = PageLoadingInfo("", false)
    private val showWaitingViewAction = Runnable { onWaitingStateChanged?.invoke(true) }
    private val dismissWaitingViewAction = Runnable { onWaitingStateChanged?.invoke(false) }

    lateinit var fullscreenContainer: FrameLayout
    var onWaitingStateChanged: ((Boolean) -> Unit)? = null
    var onPageFinished: ((String) -> Unit)? = null
    var onProgressChanged: ((Int) -> Unit)? = null
    var onFullscreenStateChanged: ((Boolean) -> Unit)? = null
    var onVideoRatioChanged: ((Fraction) -> Unit)? = null

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
            loadingInfo.set(url, true)
            disablePlayCheck()
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            Log.i(TAG, "onPageFinished, $url")
            loadingInfo.set(url, false)
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
        var videoRatio = RATIO_16_9
            set(value) {
                if (field == value) return
                field = value
                onVideoRatioChanged?.invoke(value)
                if (isInFullscreen) {
                    view?.layoutParams = generateLayoutParams()
                }
            }

        fun markNewPage() {
            lastUrl = ""
        }

        override fun onJsAlert(view: WebView, url: String, message: String?, result: JsResult): Boolean {
            result.cancel()
            return true
        }

        override fun onProgressChanged(view: WebView, progress: Int) {
            super.onProgressChanged(view, progress)
            Log.i(TAG, "$url, progress=$progress")
            if (url == URL_BLANK) {
                disablePlayCheck()
                return
            }
            if (view.url != lastUrl || progress > lastProgress) {
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
            val screenRadio = Fraction.getFraction(fullscreenContainer.width, fullscreenContainer.height)
            val compare = screenRadio.compareTo(videoRatio)
            return if (compare == 0) LP(screenRadio.numerator, screenRadio.denominator, Gravity.CENTER)
            else if (compare > 0) LP(screenRadio.denominator * videoRatio.numerator / videoRatio.denominator, screenRadio.denominator, Gravity.CENTER)
            else LP(screenRadio.numerator, screenRadio.numerator * videoRatio.denominator / videoRatio.numerator, Gravity.CENTER)
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
            resetPage(url)
            setVideoSize(0, 0)
            if (requestedUrl == url) {
                settings.apply {
                    loadsImagesAutomatically = false
                    blockNetworkImage = true
                    userAgentString = WebpageAdapterManager.get(url).userAgent()
                }
                chromeClient.apply {
                    videoRatio = RATIO_16_9
                }
                settingsExtension?.apply {
                    setPicModel(IX5WebSettingsExtension.PicModel_NoPic)
                }
                disablePlayCheck()
                chromeClient.markNewPage()
                Log.i(TAG, "Load url $url")
                super.loadUrl(url)
            } else {
                Log.i(TAG, "New url requested, ignore.")
            }
        }
    }

    private suspend fun resetPage(destUrl: String) {
        Log.i(TAG, "Resetting page...")
        val cost = measureTimeMillis {
            super.loadUrl(URL_BLANK)
            while (loadingInfo.url != URL_BLANK && !loadingInfo.isPageLoading) {
                if (destUrl != requestedUrl) {
                    Log.i(TAG, "Requested url changed, cancel.")
                    break
                }
                delay(CHECK_PAGE_LOADING_INTERVAL)
            }
            if (destUrl == requestedUrl) {
                val endTime = SystemClock.uptimeMillis() + BLANK_PAGE_WAIT
                while (SystemClock.uptimeMillis() < endTime) {
                    if (destUrl != requestedUrl) {
                        Log.i(TAG, "Requested url changed, cancel.")
                        break
                    }
                    delay(CHECK_PAGE_LOADING_INTERVAL)
                }
            }
        }
        Log.i(TAG, "Done Resetting, cost ${cost}ms.")
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

    fun setVideoRatio(ratio: Fraction) {
        chromeClient.videoRatio = ratio
    }

    fun getVideoRatio() = chromeClient.videoRatio

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
        post(dismissWaitingViewAction)
    }

    @JavascriptInterface
    fun setVideoSize(width: Int, height: Int) {
        videoSize.set(width, height)
    }

    fun getVideoSize() = videoSize

    private fun adjustWideViewPort() {
        var level = 0
        while (canZoomOut() && level < 3) {
            zoomOut()
            ++level
        }
    }

    private class PageLoadingInfo(var url: String, var isPageLoading: Boolean) {
        fun set(url: String, isPageLoading: Boolean) {
            this.url = url
            this.isPageLoading = isPageLoading
        }
    }
}