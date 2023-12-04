package com.hxh19950701.webviewtvlive

data class Playlist(
    var title: String = "",
    val groups: MutableList<ChannelGroup> = mutableListOf()
) {

    companion object {
        fun createFromAllChannels(title: String, allChannels: Collection<Channel>?): Playlist {
            val groups: MutableList<ChannelGroup> = mutableListOf()
            allChannels?.forEach {
                val channelGroup = groups.firstOrNull { group -> group.name == it.groupName }
                    ?: ChannelGroup(it.groupName).apply { groups.add(this) }
                channelGroup.channels.add(it)
            }
            return Playlist(title, groups)
        }

        fun Playlist?.firstChannel(): Channel? {
            if (this == null) return null
            if (groups.isEmpty()) return null
            else groups.forEach { if (it.channels.isNotEmpty()) return it.channels[0] }
            return null
        }
    }

    fun getAllChannels(): List<Channel> {
        val allChannels = mutableListOf<Channel>()
        groups.forEach { allChannels.addAll(it.channels) }
        return allChannels
    }

    fun indexOf(c: Channel): Pair<Int, Int>? {
        for ((i, group) in groups.withIndex()) {
            if (group.name == c.groupName) {
                for ((j, channel) in groups[i].channels.withIndex()) {
                    if (channel.name == c.name) return Pair(i, j)
                }
            }
        }
        return null
    }
}