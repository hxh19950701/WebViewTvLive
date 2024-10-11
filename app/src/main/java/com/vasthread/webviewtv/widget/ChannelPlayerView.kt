package com.vasthread.webviewtv.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import com.vasthread.webviewtv.R
import com.vasthread.webviewtv.playlist.Channel
import com.vasthread.webviewtv.settings.SettingsManager

class ChannelPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "ChannelPlayer"
    }

    private val webView: WebpageAdapterWebView
    private val waitingView: WaitingView
    private val channelBarView: ChannelBarView

    var channel: Channel? = null
        set(value) {
            if (field == value) return
            field = value
            if (value == null) {
                webView.loadUrl(WebpageAdapterWebView.URL_BLANK)
                channelBarView.dismiss()
            } else {
                webView.loadUrl(value.url)
                channelBarView.setCurrentChannelAndShow(value)
            }
        }
    var clickCallback: ((Float, Float) -> Unit)? = null
    var dismissAllViewCallback: (() -> Unit)? = null
    var onChannelReload: ((Channel) -> Unit)? = null
    var onVideoRatioChanged: ((Boolean) -> Unit)? = null

    private val gestureDetector = GestureDetector(context, object : GestureDetector.OnGestureListener {

        override fun onDown(e: MotionEvent) = true

        override fun onShowPress(e: MotionEvent) = Unit

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            clickCallback?.invoke(e.x, e.y)
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float) = false

        override fun onLongPress(e: MotionEvent) = Unit

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float) = false

    })

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            defaultFocusHighlightEnabled = false
        }
        LayoutInflater.from(context).inflate(R.layout.widget_channel_player, this)
        setBackgroundColor(Color.BLACK)
        webView = findViewById(R.id.webView)
        channelBarView = findViewById(R.id.channelBarView)
        waitingView = findViewById(R.id.waitingView)
        waitingView.playerView = this
        webView.apply {
            fullscreenContainer = this@ChannelPlayerView.findViewById(R.id.fullscreenContainer)
            onPageFinished = {}
            onProgressChanged = { channelBarView.setProgress(it) }
            onFullscreenStateChanged = {}
            onWaitingStateChanged = { waitingView.visibility = if (it) VISIBLE else GONE }
            onVideoRatioChanged = { this@ChannelPlayerView.onVideoRatioChanged?.invoke(it == WebpageAdapterWebView.RATIO_16_9) }
        }
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        return webView.requestFocus(direction, previouslyFocusedRect)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        return if (SettingsManager.isWebViewTouchable()) {
            dismissAllViewCallback?.invoke()
            super.dispatchTouchEvent(event)
        } else {
            gestureDetector.onTouchEvent(event)
        }
    }

    override fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
        return false
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return false
    }

    fun refreshChannel() {
        webView.loadUrl(channel!!.url)
    }

    fun setVideoRatio(is_16_9: Boolean) {
        webView.setVideoRatio(if (is_16_9) WebpageAdapterWebView.RATIO_16_9 else WebpageAdapterWebView.RATIO_4_3)
    }

    fun getVideoSize() = webView.getVideoSize()
}