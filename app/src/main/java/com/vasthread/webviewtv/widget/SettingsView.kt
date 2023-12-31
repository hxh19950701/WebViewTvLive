package com.vasthread.webviewtv.widget

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vasthread.webviewtv.R
import com.vasthread.webviewtv.activity.TbsDebugActivity
import com.vasthread.webviewtv.misc.adjustValue
import com.vasthread.webviewtv.playlist.PlaylistManager
import com.vasthread.webviewtv.settings.SettingItem
import com.vasthread.webviewtv.settings.SettingsManager

class SettingsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val rvSettings: RecyclerView

    private val settings = arrayOf(
        SettingItem(
            "频道列表",
            SettingsManager.getPlaylistNames(),
            SettingsManager.getSelectedPlaylistPosition(),
            onItemSelect = SettingsManager::setSelectedPlaylistPosition
        ),
        SettingItem(
            "刷新频道列表",
            onClick = { PlaylistManager.setLastUpdate(0, true) }
        ),
        SettingItem(
            "Tbs 调试界面",
            onClick = { context.startActivity(Intent(context, TbsDebugActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
        ),
        SettingItem(
            "操作 WebView",
            arrayOf("关", "开"),
            if (SettingsManager.isWebViewTouchable()) 1 else 0,
            onItemSelect = {SettingsManager.setWebViewTouchable(it != 0)}
        )
    )

    init {
        setBackgroundResource(R.drawable.bg)
        LayoutInflater.from(context).inflate(R.layout.widget_settings, this)
        rvSettings = findViewById(R.id.rvSettings)
        rvSettings.adapter = SettingsAdapter()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE) {
            post { rvSettings.getChildAt(0)?.requestFocus() }
        }
    }

    private inner class SettingsAdapter : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_settings, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = settings.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(settings[position])
        }

    }

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val btnLeft: Button = itemView.findViewById(R.id.btnLeft)
        private val btnRight: Button = itemView.findViewById(R.id.btnRight)
        private val tvItem: TextView = itemView.findViewById(R.id.tvItem)
        private val llItem: LinearLayout = itemView.findViewById(R.id.llItem)
        private lateinit var setting: SettingItem

        init {
            itemView.setOnClickListener { setting.onClick?.invoke() }
            itemView.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        adjustItem(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
                    }
                    true
                } else {
                    false
                }
            }
            btnLeft.setOnClickListener { adjustItem(false) }
            btnRight.setOnClickListener { adjustItem(true) }
        }

        fun bind(setting: SettingItem) {
            this.setting = setting
            tvTitle.text = setting.title
            if (setting.items.isNullOrEmpty()) {
                llItem.visibility = GONE
            } else {
                llItem.visibility = VISIBLE
                tvItem.text = setting.items[setting.selectedItemPosition]
            }
        }

        private fun adjustItem(next: Boolean) {
            if (setting.items.isNullOrEmpty()) return
            var selectedItemPosition = setting.selectedItemPosition
            selectedItemPosition = adjustValue(selectedItemPosition, setting.items!!.size, next)
            setting.selectedItemPosition = selectedItemPosition
            tvItem.text = setting.items!![selectedItemPosition]
            setting.onItemSelect?.invoke(selectedItemPosition)
        }

    }
}