package com.hxh19950701.webviewtvlive

import com.google.gson.GsonBuilder
import java.io.File

fun MutableCollection<Channel>.addChannels(name: String, vararg channels: Channel): MutableCollection<Channel> {
    channels.forEach { it.groupName = name }
    addAll(channels)
    return this
}

fun MutableCollection<Channel>.extractGroup(groupName: String): Array<Channel> {
    return filter { it.groupName == groupName }.toTypedArray()
}

fun savePlaylist(playlist: Playlist) {
    val json = gson.toJson(playlist)
    val file = File("channels/${playlist.title}.json")
    if (!file.parentFile.exists()) file.parentFile.mkdir()
    file.writeText(json)
}

val gson = GsonBuilder().setPrettyPrinting().create()!!