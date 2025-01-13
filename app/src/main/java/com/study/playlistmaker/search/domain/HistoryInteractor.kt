package com.study.playlistmaker.search.domain

import com.study.playlistmaker.search.domain.model.Track

interface HistoryInteractor {
    val currentHistory: MutableList<Track>

    fun addTrackToHistory(track: Track)
    fun clearHistory()
}
