package com.vasthread.webviewtv.playlist

import com.google.gson.annotations.SerializedName
import com.vasthread.webviewtv.settings.SettingsManager

data class Channel @JvmOverloads constructor(
    var name: String = "",
    @SerializedName("group")
    var groupName: String = "",
    var urls: List<String> = emptyList(),
) {

    val url: String
        get() {
            var index = SettingsManager.getChannelLastSourceIndex(name)
            if (index >= urls.size || index < 0) index = 0
            return urls[index]
        }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun toString(): String {
        return "name=$name, groupName=$groupName, urls=$urls"
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + groupName.hashCode()
        return result
    }
}