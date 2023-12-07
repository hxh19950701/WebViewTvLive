package com.hxh19950701.webviewtvlive.widget

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.webkit.JavascriptInterface
import android.widget.FrameLayout
import com.hxh19950701.webviewtvlive.R
import com.hxh19950701.webviewtvlive.adapter.WebpageAdapter
import com.hxh19950701.webviewtvlive.adapter.WebpageAdapterManager
import com.hxh19950701.webviewtvlive.playlist.Channel
import com.hxh19950701.webviewtvlive.settings.SettingsManager
import com.tencent.smtt.export.external.interfaces.ConsoleMessage
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

class ChannelPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "ChannelPlayer"
    }

    private val webView: WebpageAdapterWebView
    private val channelBarView: ChannelBarView
    var activity: Activity? = null
    var channel: Channel? = null
        set(value) {
            if (field == value) return
            field = value
            if (value == null) {
                webView.stopLoading()
                channelBarView.requestDismiss()
            } else {
                webView.loadUrl(value.url)
                channelBarView.setCurrentChannelAndShow(value)
            }
        }
    var dismissAllViewCallback: (() -> Unit)? = null

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
        webView.apply {
            onPageFinished = { channelBarView.requestDismiss() }
            onProgressChanged = { channelBarView.setProgress(progress) }
            onFullscreenStateChanged = { activity?.window?.decorView?.systemUiVisibility =
                if(it) View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN else View.SYSTEM_UI_FLAG_FULLSCREEN
            }
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
}