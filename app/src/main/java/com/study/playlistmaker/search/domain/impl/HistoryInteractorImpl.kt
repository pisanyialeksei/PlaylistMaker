package com.study.playlistmaker.search.domain.impl

import com.study.playlistmaker.search.data.HistoryRepository
import com.study.playlistmaker.search.domain.HistoryInteractor
import com.study.playlistmaker.search.domain.model.Track

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
