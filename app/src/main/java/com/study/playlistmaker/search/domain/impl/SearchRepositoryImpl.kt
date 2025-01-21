package com.study.playlistmaker.search.domain.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.reflect.TypeToken
import com.study.playlistmaker.gson
import com.study.playlistmaker.search.data.dto.SearchRequest
import com.study.playlistmaker.search.data.dto.SearchResponse
import com.study.playlistmaker.search.data.network.NetworkClient
import com.study.playlistmaker.search.domain.SearchRepository
import com.study.playlistmaker.search.domain.model.Track

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val sharedPreferences: SharedPreferences,
) : SearchRepository {

    private val historyKey = "SEARCH_HISTORY"

    override val currentHistory: MutableList<Track>
        get() = getHistory()

    override fun searchTracks(query: String): List<Track>? {
        val response = networkClient.doRequest(SearchRequest(query))
        return if (response.resultCode == 200) {
            (response as? SearchResponse)?.results?.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            } ?: emptyList()
        } else {
            null
        }
    }

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
