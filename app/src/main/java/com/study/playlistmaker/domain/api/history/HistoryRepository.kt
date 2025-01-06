package com.study.playlistmaker.domain.api.history

import com.study.playlistmaker.domain.models.Track

interface HistoryRepository {
    val currentHistory: MutableList<Track>

    fun addTrackToHistory(track: Track)
    fun clearHistory()
}
