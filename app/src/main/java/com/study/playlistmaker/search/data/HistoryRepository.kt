package com.study.playlistmaker.search.data

import com.study.playlistmaker.search.domain.model.Track

interface HistoryRepository {
    val currentHistory: MutableList<Track>

    fun addTrackToHistory(track: Track)
    fun clearHistory()
}
