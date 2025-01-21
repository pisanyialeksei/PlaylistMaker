package com.study.playlistmaker.search.domain

import com.study.playlistmaker.search.domain.model.Track

interface SearchRepository {

    val currentHistory: List<Track>

    fun searchTracks(query: String): List<Track>?
    fun addTrackToHistory(track: Track)
    fun clearHistory()
}
