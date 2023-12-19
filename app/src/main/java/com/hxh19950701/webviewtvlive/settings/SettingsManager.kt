package com.hxh19950701.webviewtvlive.settings

import com.hxh19950701.webviewtvlive.playlist.PlaylistManager
import com.hxh19950701.webviewtvlive.misc.preference

object SettingsManager {

    private const val KEY_WEBVIEW_TOUCHABLE = "webview_touchable"

    fun getPlaylistNames(): Array<String> {
        val builtInPlaylists = PlaylistManager.getBuiltInPlaylists()
        val names = arrayOfNulls<String>(PlaylistManager.getBuiltInPlaylists().size)
        for (i in names.indices) {
            names[i] = builtInPlaylists[i].first
        }
        return names.requireNoNulls()
    }

    fun getSelectedPlaylistPosition(): Int {
        val playlistUrl = PlaylistManager.getPlaylistUrl()
        val builtInPlaylists = PlaylistManager.getBuiltInPlaylists()
        for (i in builtInPlaylists.indices) {
            if (builtInPlaylists[i].second == playlistUrl) {
                return i
            }
        }
        return 0
    }

    fun setSelectedPlaylistPosition(position: Int) {
        val builtInPlaylists = PlaylistManager.getBuiltInPlaylists()
        PlaylistManager.setPlaylistUrl(builtInPlaylists[position].second)
    }

    fun setWebViewTouchable(touchable: Boolean) {
        preference.edit().putBoolean(KEY_WEBVIEW_TOUCHABLE, touchable).apply()
    }

    fun isWebViewTouchable(): Boolean {
        return preference.getBoolean(KEY_WEBVIEW_TOUCHABLE, false)
    }
}