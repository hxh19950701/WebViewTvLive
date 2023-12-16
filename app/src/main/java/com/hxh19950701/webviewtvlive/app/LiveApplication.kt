package com.hxh19950701.webviewtvlive.app

import android.app.Application
import android.util.Log
import com.hxh19950701.webviewtvlive.misc.setApplication
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.tencent.smtt.sdk.TbsDownloader
import com.tencent.smtt.sdk.TbsListener

class LiveApplication : Application() {

    companion object {
        const val TAG = "LiveApplication"
    }

    override fun onCreate() {
        super.onCreate()
        setApplication(this)
        cacheDir.listFiles()?.forEach { it.delete() }
        QbSdk.setDownloadWithoutWifi(true)
        QbSdk.setTbsListener(object : TbsListener {
            override fun onDownloadFinish(i: Int) {
                Log.d(TAG, "tbs内核下载完成回调: $i")
                //tbs内核下载完成回调
                //但是只有i等于100才算完成，否则失败
                //此时大概率可能由于网络问题
                //如果失败可增加网络监听器
            }

            override fun onInstallFinish(i: Int) {
                Log.d(TAG, "内核安装完成回调: $i")
                //内核安装完成回调，通常到这里也算安装完成，但是在
            }

            override fun onDownloadProgress(i: Int) {
                //下载进度监听
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