package com.vasthread.webviewtv.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vasthread.webviewtv.R
import com.vasthread.webviewtv.widget.WebpageAdapterWebView

class TbsDebugActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tbs_debug)
        val webView = findViewById<WebpageAdapterWebView>(R.id.webView)
        webView.loadUrl("https://debugtbs.qq.com")
    }
}