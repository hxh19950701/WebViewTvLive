package com.vasthread.webviewtv.widget

import android.content.Context
import android.graphics.Point
import android.os.SystemClock
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.vasthread.webviewtv.R
import com.vasthread.webviewtv.misc.getTrafficBytes
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.system.measureTimeMillis

@Suppress("PrivatePropertyName", "LocalVariableName")
class ChannelSettingsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val UPDATE_PERIOD = 1000L
    }

    private val tvSource: TextView
    private val llSource: LinearLayout
    private val btnSource1: Button
    private val btnSource2: Button
    private val rbAspectRatio_16_9: Button
    private val rbAspectRatio_4_3: Button
    private val tvVideoSize: TextView
    private val tvCurrentTime: TextView
    private val tvCurrentNetworkSpeed: TextView

    var onChannelSourceSelected: ((Int) -> Unit)? = null
    var onAspectRatioSelected: ((Boolean) -> Unit)? = null
    var onGetVideoSize: (() -> Point)? = null

    private val sdf = SimpleDateFormat("HH:mm:ss", Locale.CHINA)
    private var lastTrafficBytes = 0L
    private var lastTrafficBytesUpdateTime = 0L
    private lateinit var updateAction: Runnable

    init {
        isClickable = true
        isFocusable = false
        LayoutInflater.from(context).inflate(R.layout.widget_channel_settings, this)

        tvSource = findViewById(R.id.tvSource)
        llSource = findViewById(R.id.llSource)
        btnSource1 = findViewById(R.id.btnSource1)
        btnSource2 = findViewById(R.id.btnSource2)
        rbAspectRatio_16_9 = findViewById(R.id.rbAspectRatio_16_9)
        rbAspectRatio_4_3 = findViewById(R.id.rbAspectRatio_4_3)
        tvVideoSize = findViewById(R.id.tvVideoSize)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)
        tvCurrentNetworkSpeed = findViewById(R.id.tvCurrentNetworkSpeed)

        btnSource1?.setOnClickListener { onChannelSourceSelected?.invoke(0) }
        btnSource2?.setOnClickListener { onChannelSourceSelected?.invoke(1) }
        rbAspectRatio_16_9.setOnClickListener { onAspectRatioSelected?.invoke(true) }
        rbAspectRatio_4_3.setOnClickListener { onAspectRatioSelected?.invoke(false) }

        updateAction = Runnable {
            val time = measureTimeMillis {
                updateNetworkSpeed()
                tvCurrentTime.text = sdf.format(System.currentTimeMillis())
                onGetVideoSize?.invoke()?.let {
                    tvVideoSize.text =
                        if (it.x == 0 || it.y == 0) context.getString(R.string.unknown)
                        else "${it.x}x${it.y}"
                }
            }
            postDelayed(updateAction, UPDATE_PERIOD - time)
        }
        setSelectedAspectRatio(true)
    }

    private fun updateNetworkSpeed() {
        val trafficBytes = getTrafficBytes()
        if (lastTrafficBytes != 0L) {
            val duration = (SystemClock.uptimeMillis() - lastTrafficBytesUpdateTime) / 1000f
            var speed = (trafficBytes - lastTrafficBytes) / duration / 1024
            if (speed >= 1000F) {
                speed /= 1024
                var speedString = "%.1f".format(speed)
                if (speedString.endsWith(".0")) {
                    speedString = speedString.substring(0, speedString.length - 2)
                }
                tvCurrentNetworkSpeed.text = "$speedString MB/s"
            } else {
                tvCurrentNetworkSpeed.text = "%d KB/s".format(speed.toInt())
            }
        }
        lastTrafficBytes = trafficBytes
        lastTrafficBytesUpdateTime = SystemClock.uptimeMillis()
    }

    fun setSelectedAspectRatio(is_16_9: Boolean) {
        rbAspectRatio_16_9.isSelected = is_16_9
        rbAspectRatio_4_3.isSelected = !is_16_9
    }

    fun setSelectedChannelSource(sourceIndex: Int, sourceSize: Int) {
        btnSource1.isSelected = sourceIndex == 0
        btnSource2.isSelected = sourceIndex == 1
        btnSource2.isVisible = sourceSize >= 2
        tvSource.isVisible = sourceSize >= 2
        llSource.isVisible = sourceSize >= 2
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == VISIBLE) {
            post(updateAction)
            post { (if (rbAspectRatio_16_9.isSelected) rbAspectRatio_16_9 else rbAspectRatio_4_3).requestFocus() }
        } else {
            removeCallbacks(updateAction)
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_MENU && event.action == KeyEvent.ACTION_UP) {
            onAspectRatioSelected?.invoke(rbAspectRatio_16_9.isSelected)
        }
        return super.dispatchKeyEvent(event)
    }

}