package com.hxh19950701.webviewtvlive.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.JavascriptInterface
import com.hxh19950701.webviewtvlive.adapter.WebpageAdapterManager
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("unused", "DEPRECATION")
@SuppressLint("SetJavaScriptEnabled")
class WebpageAdapterWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "WebpageAdapterWebView"
        const val URL_BLANK = "chrome://blank"
        private const val SHOW_WAITING_VIEW_DELAY = 2000L
        private const val MAX_ZOOM_OUT_LEVEL = 3
        private const val MAX_WAITING_MS = 8000L
    }

    private var isInFullscreen = false
    private var requestedUrl = URL_BLANK
    private var isLoading = false
    private val mainLooper = Looper.getMainLooper()
    private val stopLoadingAction = Runnable {
        if (isLoading) {
            Log.i(TAG, "Loading time is too long, stop loading.")
            stopLoading()
        }
    }
    private val showWaitingViewAction = Runnable { onWaitingStateChanged?.invoke(true) }

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
            Log.i(TAG, "onPageStarted, $url")
            super.onPageStarted(view, url, favicon)
            disablePlayCheck()
            isLoading = true
        }

        override fun onPageFinished(view: WebView, url: String) {
            Log.i(TAG, "onPageFinished, $url")
            super.onPageFinished(view, url)
            if (getRequestedUrl() != url) return
            isLoading = false
            removeCallbacks(stopLoadingAction)
            onPageLoadFinished()
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

        override fun onJsAlert(view: WebView, url: String, message: String?, result: JsResult): Boolean {
            result.cancel()
            return true
        }

        override fun onProgressChanged(view: WebView, progress: Int) {
            Log.i(TAG, "onProgressChanged, $progress")
            super.onProgressChanged(view, progress)
            disablePlayCheck()
            if (getRequestedUrl() != getCurrentUrl()) return
            onProgressChanged?.invoke(progress)
            adjustWideViewPort()
            if (progress == 100 && isLoading) {
                removeCallbacks(stopLoadingAction)
                postDelayed(stopLoadingAction, MAX_WAITING_MS)
            }
        }

        override fun onConsoleMessage(msg: ConsoleMessage): Boolean {
            Log.i(TAG, msg.message())
            return true
        }

        override fun onShowCustomView(view: View, callback: IX5WebChromeClient.CustomViewCallback) {
            this.view = view
            this.callback = callback
            addView(view)
            isInFullscreen = true
            onFullscreenStateChanged?.invoke(true)
        }

        override fun onHideCustomView() {
            removeView(view)
            callback?.onCustomViewHidden()
            view = null
            callback = null
            isInFullscreen = false
            onFullscreenStateChanged?.invoke(false)
        }

        override fun onPermissionRequest(p0: PermissionRequest?) {
            super.onPermissionRequest(p0)
        }
    }

    private val chromeClientExtension = object : ProxyWebChromeClientExtension() {

        override fun jsRequestFullScreen() {
            Log.i(TAG, "jsRequestFullScreen")
            super.jsRequestFullScreen()
        }

        override fun h5videoRequestFullScreen(p0: String?) {
            Log.i(TAG, "h5videoRequestFullScreen")
            super.h5videoRequestFullScreen(p0)
        }

        override fun onPermissionRequest(origin: String, resources: Long, callback: MediaAccessPermissionsCallback): Boolean {
            Log.i(TAG, "onPermissionRequest, origin=$origin, resources=$resources")
            callback.invoke(origin, MediaAccessPermissionsCallback.ALLOW_VIDEO_CAPTURE or MediaAccessPermissionsCallback.ALLOW_AUDIO_CAPTURE, true)
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
            //setSupportZoom(true)
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
        if (Looper.myLooper() != mainLooper) {
            post { loadUrl(url) }
        } else {
            if (isLoading) stopLoading()
            settings.apply {
                loadsImagesAutomatically = false
                blockNetworkImage = true
                userAgentString = WebpageAdapterManager.get(url).userAgent()
            }
            settingsExtension?.apply {
                setPicModel(IX5WebSettingsExtension.PicModel_NoPic)
            }
            Log.i(TAG, "Load url $url")
            this.requestedUrl = url
            super.loadUrl(requestedUrl)
        }
    }

    override fun reload() {
        disablePlayCheck()
        super.reload()
    }

    fun getRequestedUrl() = requestedUrl

    fun getCurrentUrl(): String = url

    fun isInFullscreen() = isInFullscreen

    override fun stopLoading() {
        disablePlayCheck()
        removeCallbacks(stopLoadingAction)
        super.stopLoading()
    }

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
        if (Looper.myLooper() == mainLooper) {
            disablePlayCheck()
            enablePlayCheck()
        } else {
            post { notifyVideoPlaying() }
        }
    }

    @JavascriptInterface
    fun enablePlayCheck() {
        postDelayed(showWaitingViewAction, SHOW_WAITING_VIEW_DELAY)
    }

    private fun disablePlayCheck() {
        onWaitingStateChanged?.invoke(false)
        removeCallbacks(showWaitingViewAction)
    }

    private fun onPageLoadFinished() {
        disablePlayCheck()
        if (getRequestedUrl() != getCurrentUrl()) return
        adjustWideViewPort()
        evaluateJavascript(WebpageAdapterManager.get(url).javascript(), null)
        onPageFinished?.invoke(url)
    }

    private fun adjustWideViewPort() {
        var level = 0
        while (canZoomOut() && level < 3) {
            zoomOut()
            ++level
        }
    }
}