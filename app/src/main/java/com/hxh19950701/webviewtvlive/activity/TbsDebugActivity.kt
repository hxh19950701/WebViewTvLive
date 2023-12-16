package com.hxh19950701.webviewtvlive.activity

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.hxh19950701.webviewtvlive.R
import com.hxh19950701.webviewtvlive.widget.WebpageAdapterWebView
import com.tencent.smtt.sdk.WebView

class TbsDebugActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tbs_debug)
        val webView = findViewById<WebpageAdapterWebView>(R.id.webView)
        webView.loadUrl("https://debugtbs.qq.com")
    }
}