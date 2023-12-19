package com.hxh19950701.webviewtvlive.app

import android.app.Application
import android.util.Log
import com.hxh19950701.webviewtvlive.misc.setApplication
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.tencent.smtt.sdk.TbsListener


class LiveApplication : Application() {

    companion object {
        private const val TAG = "LiveApplication"
    }

    override fun onCreate() {
        super.onCreate()
        setApplication(this)
        val map = mutableMapOf<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
        QbSdk.setTbsListener(object : TbsListener {
            override fun onDownloadFinish(i: Int) {
                Log.d(TAG, "tbs内核下载完成回调: $i")
            }

            override fun onInstallFinish(i: Int) {
                Log.d(TAG, "内核安装完成回调: $i")
            }

            override fun onDownloadProgress(i: Int) {
                Log.d(TAG, "下载进度监听: $i")
            }
        })
        QbSdk.initX5Environment(this, object : PreInitCallback {
            override fun onCoreInitFinished() {
                Log.i(TAG, "onCoreInitFinished")
            }

            override fun onViewInitFinished(isX5: Boolean) {
                Log.i(TAG, "onViewInitFinished, isX5=$isX5")
            }
        })
    }
}