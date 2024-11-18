package com.study.playlistmaker.search

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.study.playlistmaker.track.Track

class SearchHistoryManager(
    private val sharedPreferences: SharedPreferences,
) {

    private val historyKey = "SEARCH_HISTORY"

    val currentHistory: MutableList<Track>
        get() = getTracksFromPreferences()

    fun addTrackToHistory(track: Track) {
        val updatedHistory = currentHistory
        updatedHistory.removeAll { it.trackId == track.trackId }
        updatedHistory.add(0, track)
        if (updatedHistory.size > 10) {
            updatedHistory.removeAt(updatedHistory.size - 1)
        }
        putTracksToPreferences(updatedHistory)
    }

    fun clearHistory() {
        sharedPreferences.edit {
            remove(historyKey)
        }
    }

    private fun getTracksFromPreferences(): MutableList<Track> {
        val json = sharedPreferences.getString(historyKey, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    private fun putTracksToPreferences(tracks: MutableList<Track>) {
        val json = Gson().toJson(tracks)
        sharedPreferences.edit {
            putString(historyKey, json)
        }
    }
}
