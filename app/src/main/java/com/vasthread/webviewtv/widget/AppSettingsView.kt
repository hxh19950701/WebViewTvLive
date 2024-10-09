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

class AppSettingsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val rvSettings: RecyclerView

    private val settings = arrayOf(
        SettingItem(
            R.string.channel_list,
            SettingsManager.getPlaylistNames(),
            SettingsManager.getSelectedPlaylistPosition(),
            onItemSelect = SettingsManager::setSelectedPlaylistPosition
        ),
        SettingItem(
            R.string.max_loading_time,
            context.resources.getStringArray(R.array.loading_time_text),
            context.resources.getIntArray(R.array.loading_time_value).indexOf(SettingsManager.getMaxLoadingTime()),
            onItemSelect = {
                SettingsManager.setMaxLoadingTime(context.resources.getIntArray(R.array.loading_time_value)[it])
            }
        ),
        SettingItem(
            R.string.refresh_channel_list,
            onClick = { PlaylistManager.setLastUpdate(0, true) }
        ),
        SettingItem(
            R.string.tbs_debug,
            onClick = {
                context.startActivity(Intent(context, TbsDebugActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        ),
        SettingItem(
            R.string.web_view_touchable,
            arrayOf(context.getString(R.string.off), context.getString(R.string.on)),
            if (SettingsManager.isWebViewTouchable()) 1 else 0,
            onItemSelect = { SettingsManager.setWebViewTouchable(it != 0) }
        )
    )

    init {
        isClickable = true
        isFocusable = false
        setBackgroundResource(R.drawable.bg)
        LayoutInflater.from(context).inflate(R.layout.widget_settings, this)
        rvSettings = findViewById(R.id.rvSettings)
        rvSettings.adapter = SettingsAdapter()
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
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
            tvTitle.text = rootView.context.getString(setting.titleRes)
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