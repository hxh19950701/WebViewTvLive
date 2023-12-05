package com.hxh19950701.webviewtvlive.playlist

import com.google.gson.GsonBuilder
import com.hxh19950701.webviewtvlive.application
import com.hxh19950701.webviewtvlive.preference
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

object PlaylistManager {

    private const val CACHE_EXPIRATION_MS = 24 * 60 * 60 * 1000L
    private const val KEY_PLAYLIST_URL = "playlist_url"
    private const val KEY_LAST_UPDATE = "last_update"

    private val client = OkHttpClient()
    private val gson = GsonBuilder().setPrettyPrinting().create()!!
    private val file = File(application.filesDir, "playlist.json")
    private val builtInPlaylists = listOf(
        Pair("中央&湖南", "https://raw.githubusercontent.com/hxh19950701/WebViewTvLive/main/app/channels/%E5%A4%AE%E8%A7%86%26%E6%B9%96%E5%8D%97.json"),
        Pair("完整", "https://raw.githubusercontent.com/hxh19950701/WebViewTvLive/main/app/channels/%E5%AE%8C%E6%95%B4.json"),
    )

    fun getBuiltInPlaylists() = builtInPlaylists

    fun setPlaylistUrl(url: String) {
        preference.edit()
            .putString(KEY_PLAYLIST_URL, url)
            .putLong(KEY_LAST_UPDATE, 0)
            .apply()
    }

    fun getPlaylistUrl() = preference.getString(KEY_PLAYLIST_URL, builtInPlaylists[0].second)!!

    fun setLastUpdate(time: Long) = preference.edit().putLong(KEY_LAST_UPDATE, time).apply()

    private fun hasLocalPlaylist(): Boolean {
        if (!file.isFile) return false
        val json = file.readText()
        return try {
            gson.fromJson(json, Playlist::class.java)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun updatePlaylist() {
        do {
            try {
                val request = Request.Builder().url(getPlaylistUrl()).get().build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    file.writeText(response.body!!.string())
                    setLastUpdate(System.currentTimeMillis())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } while (!hasLocalPlaylist())
    }

    fun loadPlaylist(): Playlist {
        val updateTime = preference.getLong(KEY_LAST_UPDATE, 0L)
        if (System.currentTimeMillis() - updateTime > CACHE_EXPIRATION_MS || !hasLocalPlaylist()) {
            updatePlaylist()
        }
        val json = file.readText()
        return gson.fromJson(json, Playlist::class.java)
    }

}