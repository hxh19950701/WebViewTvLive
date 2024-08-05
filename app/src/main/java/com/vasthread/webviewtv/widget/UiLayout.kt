package com.vasthread.webviewtv.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.WindowInsets
import android.widget.FrameLayout

class UiLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val cutout = insets.displayCutout
            setPadding(
                cutout?.safeInsetLeft ?: 0,
                cutout?.safeInsetTop ?: 0,
                cutout?.safeInsetRight ?: 0,
                cutout?.safeInsetBottom ?: 0
            )
        }
        return super.onApplyWindowInsets(insets)
    }
}