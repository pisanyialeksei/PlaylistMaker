package com.study.playlistmaker.search.domain

import com.study.playlistmaker.search.domain.model.Track

interface SearchInteractor {

    val currentHistory: List<Track>

    fun searchTracks(query: String, consumer: TracksConsumer)
    fun addTrackToHistory(track: Track)
    fun clearHistory()

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?)
    }
}
