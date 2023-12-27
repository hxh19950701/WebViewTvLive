package com.vasthread.webviewtv

import com.google.gson.GsonBuilder
import com.vasthread.webviewtv.playlist.Channel
import java.io.File

const val VERSION_NAME = "1.0"

fun MutableCollection<Channel>.addChannels(name: String, vararg channels: Channel): MutableCollection<Channel> {
    channels.forEach { add(Channel(it.name, it.url, name)) }
    return this
}

fun MutableCollection<Channel>.extractGroup(groupName: String): Array<Channel> {
    return filter { it.groupName == groupName }.toTypedArray()
}

fun savePlaylist(title: String, channels: Collection<Channel>) {
    val json = gson.toJson(channels)
    val file = File("channels/$VERSION_NAME/$title.json")
    val parentFile = file.parentFile
    if (parentFile != null && !parentFile.exists()) {
        parentFile.mkdir()
    }
    file.writeText(json)
}

val gson = GsonBuilder().setPrettyPrinting().create()!!