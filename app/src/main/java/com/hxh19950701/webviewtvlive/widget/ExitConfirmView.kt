package com.hxh19950701.webviewtvlive.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.hxh19950701.webviewtvlive.R

class ExitConfirmView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    enum class Selection { EXIT, SETTINGS }

    private val btnSettings: Button
    var onUserSelection: ((Selection) -> Unit)? = null

    init {
        gravity = Gravity.CENTER
        orientation = VERTICAL
        setBackgroundResource(R.drawable.bg)
        LayoutInflater.from(context).inflate(R.layout.widget_exit_confirm, this)
        btnSettings = findViewById(R.id.btnSettings)
        btnSettings.setOnClickListener { onUserSelection?.invoke(Selection.SETTINGS) }
        findViewById<Button>(R.id.btnExit).setOnClickListener { onUserSelection?.invoke(Selection.EXIT) }
        findViewById<TextView>(R.id.tvAndroidVersion).text = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE) {
            post { btnSettings.requestFocus() }
        }
    }
}