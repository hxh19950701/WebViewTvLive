package com.hxh19950701.webviewtvlive.widget

import android.content.Context
import android.util.AttributeSet
import com.tencent.smtt.sdk.WebView

class FixedSizeWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

}