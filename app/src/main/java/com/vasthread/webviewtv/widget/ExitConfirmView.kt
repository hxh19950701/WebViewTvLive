package com.vasthread.webviewtv.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.tencent.smtt.sdk.QbSdk
import com.vasthread.webviewtv.R

@SuppressLint("SetTextI18n")
class ExitConfirmView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    enum class Selection { EXIT, SETTINGS }

    private val btnSettings: Button
    var onUserSelection: ((Selection) -> Unit)? = null

    init {
        isClickable = true
        isFocusable = false
        gravity = Gravity.CENTER
        orientation = VERTICAL
        setBackgroundResource(R.drawable.bg)
        LayoutInflater.from(context).inflate(R.layout.widget_exit_confirm, this)
        btnSettings = findViewById(R.id.btnSettings)
        btnSettings.setOnClickListener { onUserSelection?.invoke(Selection.SETTINGS) }
        findViewById<Button>(R.id.btnExit).setOnClickListener { onUserSelection?.invoke(Selection.EXIT) }
        findViewById<TextView>(R.id.tvAppInfo).text = "App: ${context.packageManager.getPackageInfo(context.packageName, 0).versionName} | X5: ${QbSdk.getTbsVersion(context)}"
        findViewById<TextView>(R.id.tvSystemInfo).text = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == VISIBLE) {
            post { btnSettings.requestFocus() }
        }
    }
}