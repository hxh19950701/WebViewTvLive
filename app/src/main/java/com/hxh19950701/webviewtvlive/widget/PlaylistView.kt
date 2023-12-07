package com.hxh19950701.webviewtvlive.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hxh19950701.webviewtvlive.playlist.Channel
import com.hxh19950701.webviewtvlive.playlist.ChannelGroup
import com.hxh19950701.webviewtvlive.playlist.Playlist
import com.hxh19950701.webviewtvlive.playlist.Playlist.Companion.firstChannel
import com.hxh19950701.webviewtvlive.R

class PlaylistView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val btnPageUp: Button
    private val btnPageDown: Button
    private val tvGroupName: TextView
    private val rvChannels: RecyclerView

    var playlist: Playlist? = null
        set(value) {
            if (field == value) return
            field = value
            if (value == null) {
                rvChannels.adapter = null
                tvGroupName.text = null
                btnPageDown.visibility = GONE
                btnPageUp.visibility = GONE
            } else {
                currentPage = 0
                val singleGroup = value.groups.size <= 1
                btnPageDown.visibility = if (singleGroup) GONE else VISIBLE
                btnPageUp.visibility = if (singleGroup) GONE else VISIBLE
            }
        }

    var currentChannel: Channel? = null
        set(value) {
            field = value
            onChannelSelectCallback?.invoke(value!!)
        }

    private var currentPage: Int = 0
        set(value) {
            field = value
            val group = playlist!!.groups[value]
            setCurrentGroup(group)
        }
    var onChannelSelectCallback: ((Channel) -> Unit)? = null

    init {
        orientation = VERTICAL
        setBackgroundResource(R.drawable.bg)
        LayoutInflater.from(context).inflate(R.layout.widget_playlist, this)

        btnPageUp = findViewById(R.id.btnPageUp)
        btnPageDown = findViewById(R.id.btnPageDown)
        tvGroupName = findViewById(R.id.tvGroupName)
        rvChannels = findViewById(R.id.rvChannels)

        btnPageUp.setOnClickListener { currentPage = if (currentPage - 1 < 0) playlist!!.groups.size - 1 else currentPage - 1 }
        btnPageDown.setOnClickListener { currentPage = if (currentPage + 1 >= playlist!!.groups.size) 0 else currentPage + 1 }
    }

    fun previousChannel() = selectChannel(false)

    fun nextChannel() = selectChannel(true)

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> btnPageUp.performClick()
            KeyEvent.KEYCODE_DPAD_RIGHT -> btnPageDown.performClick()
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun selectChannel(next: Boolean) {
        if (currentChannel == null) {
            currentChannel = playlist.firstChannel()
        }
        if (playlist != null) {
            val index = playlist!!.indexOf(currentChannel!!)
            currentChannel = if (index == null) {
                playlist.firstChannel()
            } else {
                val channels = playlist!!.groups[index.first].channels
                val j = if (next) {
                    if (index.second + 1 >= channels.size) 0 else index.second + 1
                } else {
                    if (index.second - 1 < 0) channels.size - 1 else index.second - 1
                }
                channels[j]
            }
        }
    }

    private fun setCurrentGroup(group: ChannelGroup) {
        rvChannels.adapter = ChannelAdapter(group)
        tvGroupName.text = group.name + "(${group.channels.size})"
    }

    private fun focusCurrentChannel() {
        val adapter = rvChannels.adapter as ChannelAdapter
        val position = adapter.group.channels.indexOf(currentChannel)
        rvChannels.scrollToPosition(position)
        rvChannels.post {
            for (i in 0..<rvChannels.childCount) {
                val child = rvChannels.getChildAt(i)
                val holder = rvChannels.getChildViewHolder(child) as ViewHolder
                if (holder.channel == currentChannel) {
                    child.requestFocus()
                }
            }
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE && playlist != null && currentChannel != null) {
            val adapter = rvChannels.adapter as ChannelAdapter
            if (currentChannel!!.groupName != adapter.group.name) {
                val index = playlist!!.indexOf(currentChannel!!)
                if (index != null) {
                    currentPage = index.first
                }
            } else {
                adapter.notifyDataSetChanged()
            }
            focusCurrentChannel()
        }
    }

    private inner class ChannelAdapter(val group: ChannelGroup) : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_channel, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = group.channels.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(position + 1, group.channels[position])
        }

    }

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), OnClickListener {

        private val tvNumber: TextView = itemView.findViewById(R.id.tvNumber)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        var channel: Channel? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(number: Int, channel: Channel) {
            this.channel = channel
            val color = Color.parseColor(if (channel == currentChannel) "#D4762E" else "#EEEEEE")
            tvNumber.text = String.format("%02d", number)
            tvNumber.setTextColor(color)
            tvTitle.text = channel.name
            tvTitle.setTextColor(color)
        }

        override fun onClick(v: View) {
            currentChannel = channel
        }

    }
}