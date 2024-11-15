package com.vasthread.webviewtv

import com.google.gson.GsonBuilder
import com.vasthread.webviewtv.playlist.Channel
import java.io.File

const val VERSION_NAME = "3.0"

fun MutableCollection<Channel>.addChannels(name: String, vararg channels: Channel): MutableCollection<Channel> {
    channels.forEach { add(Channel(it.name, name, it.urls)) }
    return this
}

fun MutableCollection<Channel>.addChannels(name: String, channels: Collection<Channel>): MutableCollection<Channel> {
    channels.forEach { add(Channel(it.name, name, it.urls)) }
    return this
}

fun MutableCollection<Channel>.extractGroup(groupName: String): MutableCollection<Channel> {
    val collection = mutableListOf<Channel>()
    return filter { it.groupName == groupName }.toCollection(collection)
}

fun MutableCollection<Channel>.extractAllGroupsExclude(groupName: String): MutableCollection<Channel> {
    val collection = mutableListOf<Channel>()
    return filter { it.groupName != groupName }.toCollection(collection)
}

fun savePlaylist(title: String, channels: Collection<Channel>, path: String = "../channels/$VERSION_NAME/") {
    val json = gson.toJson(channels)
    val file = File(path, "$title.json")
    val parentFile = file.parentFile
    if (parentFile != null && !parentFile.exists()) {
        parentFile.mkdirs()
    }
    file.writeText(json)
}

val gson = GsonBuilder().setPrettyPrinting().create()!!