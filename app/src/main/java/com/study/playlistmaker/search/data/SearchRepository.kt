package com.study.playlistmaker.search.data

import com.study.playlistmaker.search.domain.model.Track

interface SearchRepository {

    val currentHistory: MutableList<Track>

    fun searchTracks(query: String): List<Track>?
    fun addTrackToHistory(track: Track)
    fun clearHistory()
}
