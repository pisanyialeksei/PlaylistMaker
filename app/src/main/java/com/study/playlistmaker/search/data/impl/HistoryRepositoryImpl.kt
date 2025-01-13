package com.study.playlistmaker.search.data.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.reflect.TypeToken
import com.study.playlistmaker.gson
import com.study.playlistmaker.search.data.HistoryRepository
import com.study.playlistmaker.search.domain.model.Track

class HistoryRepositoryImpl(private val sharedPreferences: SharedPreferences) : HistoryRepository {

    private val historyKey = "SEARCH_HISTORY"

    override val currentHistory: MutableList<Track>
        get() = getHistory()

    override fun addTrackToHistory(track: Track) {
        val updatedHistory = currentHistory
        updatedHistory.removeAll { it.trackId == track.trackId }
        updatedHistory.add(0, track)
        if (updatedHistory.size > 10) {
            updatedHistory.removeAt(updatedHistory.size - 1)
        }
        val json = gson.toJson(updatedHistory)
        sharedPreferences.edit {
            putString(historyKey, json)
        }
    }

    override fun clearHistory() {
        sharedPreferences.edit {
            remove(historyKey)
        }
    }

    private fun getHistory(): MutableList<Track> {
        val json = sharedPreferences.getString(historyKey, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(json, type)
    }
}
