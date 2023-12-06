package com.hxh19950701.webviewtvlive.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hxh19950701.webviewtvlive.R
import com.hxh19950701.webviewtvlive.playlist.PlaylistManager
import com.hxh19950701.webviewtvlive.settings.SettingItem
import com.hxh19950701.webviewtvlive.settings.SettingsManager

class SettingsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val rvSettings: RecyclerView

    private val settings = arrayOf(
        SettingItem(
            "频道列表",
            SettingsManager.getPlaylistNames(),
            SettingsManager.getSelectedPlaylistPosition(),
            null,
            SettingsManager::setSelectedPlaylistPosition
        ),
        SettingItem("刷新频道列表", onClick = { PlaylistManager.setLastUpdate(0) }),
        SettingItem(
            "操作 WebView",
            arrayOf("关", "开"),
            if (SettingsManager.isWebViewTouchable()) 1 else 0,
            null
        ) { SettingsManager.setWebViewTouchable(it != 0) },
    )

    init {
        setBackgroundResource(R.drawable.bg)
        LayoutInflater.from(context).inflate(R.layout.widget_settings, this)
        rvSettings = findViewById(R.id.rvSettings)
        rvSettings.adapter = SettingsAdapter()
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
        lateinit var setting: SettingItem

        init {
            itemView.setOnClickListener { setting.onClick?.invoke() }
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
            selectedItemPosition = if (next) {
                if (selectedItemPosition + 1 >= setting.items!!.size) 0 else selectedItemPosition + 1
            } else {
                if (selectedItemPosition - 1 < 0) setting.items!!.size - 1 else selectedItemPosition - 1
            }
            setting.selectedItemPosition = selectedItemPosition
            tvItem.text = setting.items!![selectedItemPosition]
            setting.onItemSelect?.invoke(selectedItemPosition)
        }

    }
}