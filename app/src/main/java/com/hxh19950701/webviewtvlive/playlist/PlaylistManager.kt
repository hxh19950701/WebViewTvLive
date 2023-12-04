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
    private const val DEFAULT_PLAYLIST_URL = "https://raw.githubusercontent.com/hxh19950701/WebViewTvLive/main/app/channels/%E5%A4%AE%E8%A7%86%26%E6%B9%96%E5%8D%97.json"

    private val client = OkHttpClient()
    private val gson = GsonBuilder().setPrettyPrinting().create()!!
    private val file = File(application.filesDir, "playlist.json")

    fun setPlaylistUrl(url: String) {
        preference.edit()
            .putString(KEY_PLAYLIST_URL, url)
            .putLong(KEY_LAST_UPDATE, 0)
            .apply()
    }

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
        val url = preference.getString(KEY_PLAYLIST_URL, DEFAULT_PLAYLIST_URL)!!
        do {
            try {
                val request = Request.Builder().url(url).get().build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    file.writeText(response.body!!.string())
                    preference.edit().putLong(KEY_LAST_UPDATE, System.currentTimeMillis()).apply()
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