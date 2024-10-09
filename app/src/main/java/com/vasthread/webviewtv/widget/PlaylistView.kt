package com.vasthread.webviewtv.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vasthread.webviewtv.R
import com.vasthread.webviewtv.misc.adjustValue
import com.vasthread.webviewtv.playlist.Channel
import com.vasthread.webviewtv.playlist.ChannelGroup
import com.vasthread.webviewtv.playlist.Playlist
import com.vasthread.webviewtv.playlist.Playlist.Companion.firstChannel

class PlaylistView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private val STATE_PRESSED = intArrayOf(android.R.attr.state_pressed)
        private val STATE_EMPTY = intArrayOf()
    }

    private val btnPageUp: Button
    private val btnPageDown: Button
    private val tvGroupName: TextView
    private val rvChannels: RecyclerView
    private val pbUpdating: ProgressBar

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

    var updating = false
        set(value) {
            println("updating $value")
            field = value
            pbUpdating.visibility = if (value) VISIBLE else GONE
        }

    private var currentPage: Int = 0
        set(value) {
            field = value
            val group = playlist!!.groups[value]
            setCurrentGroup(group)
        }
    var onChannelSelectCallback: ((Channel) -> Unit)? = null

    init {
        isClickable = true
        isFocusable = false
        orientation = VERTICAL
        setBackgroundResource(R.drawable.bg)
        LayoutInflater.from(context).inflate(R.layout.widget_playlist, this)

        btnPageUp = findViewById(R.id.btnPageUp)
        btnPageDown = findViewById(R.id.btnPageDown)
        tvGroupName = findViewById(R.id.tvGroupName)
        rvChannels = findViewById(R.id.rvChannels)
        pbUpdating = findViewById(R.id.pbUpdating)

        btnPageUp.setOnClickListener { turnPage(false) }
        btnPageDown.setOnClickListener { turnPage(true) }
    }

    fun previousChannel() = selectChannel(false)

    fun nextChannel() = selectChannel(true)

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            val isLeft = keyCode == KeyEvent.KEYCODE_DPAD_LEFT
            val background = (if (isLeft) btnPageUp else btnPageDown).background as StateListDrawable
            if (event.action == KeyEvent.ACTION_DOWN) {
                background.setState(STATE_PRESSED)
                turnPage(!isLeft)
            } else {
                background.setState(STATE_EMPTY)
            }
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    private fun turnPage(down: Boolean) {
        if (playlist!!.groups.size <= 1) return
        currentPage = adjustValue(currentPage, playlist!!.groups.size, down)
        rvChannels.post { rvChannels.getChildAt(0)?.requestFocus() }
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
                val j = adjustValue(index.second, channels.size, next)
                channels[j]
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCurrentGroup(group: ChannelGroup) {
        rvChannels.adapter = ChannelAdapter(group)
        tvGroupName.text = "${group.name}(${group.channels.size})"
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

    @SuppressLint("NotifyDataSetChanged")
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

            tvNumber.text = String.format("%02d", number)
            tvTitle.text = channel.name

            val isSelected = channel == currentChannel
            tvNumber.isSelected = isSelected
            tvTitle.isSelected = isSelected
        }

        override fun onClick(v: View) {
            currentChannel = channel
        }

    }
}