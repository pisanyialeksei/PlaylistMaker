package com.study.playlistmaker.domain.impl

import com.study.playlistmaker.domain.api.history.HistoryInteractor
import com.study.playlistmaker.domain.api.history.HistoryRepository
import com.study.playlistmaker.domain.models.Track

class HistoryInteractorImpl(private val repository: HistoryRepository) : HistoryInteractor {

    override val currentHistory: MutableList<Track>
        get() = repository.currentHistory

    override fun addTrackToHistory(track: Track) {
        repository.addTrackToHistory(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}
