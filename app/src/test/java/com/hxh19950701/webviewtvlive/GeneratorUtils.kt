package com.hxh19950701.webviewtvlive

import com.google.gson.GsonBuilder
import com.hxh19950701.webviewtvlive.playlist.Channel
import com.hxh19950701.webviewtvlive.playlist.Playlist
import java.io.File

fun MutableCollection<Channel>.addChannels(name: String, vararg channels: Channel): MutableCollection<Channel> {
    channels.forEach { it.groupName = name }
    addAll(channels)
    return this
}

fun MutableCollection<Channel>.extractGroup(groupName: String): Array<Channel> {
    return filter { it.groupName == groupName }.toTypedArray()
}

fun savePlaylist(title: String, channels: Collection<Channel>) {
    val json = gson.toJson(channels)
    val file = File("channels/$title.json")
    if (!file.parentFile.exists()) file.parentFile.mkdir()
    file.writeText(json)
}

val gson = GsonBuilder().setPrettyPrinting().create()!!