package com.study.playlistmaker.search.domain.impl

import com.study.playlistmaker.search.domain.SearchInteractor
import com.study.playlistmaker.search.domain.SearchRepository
import com.study.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {

    override val currentHistory: List<Track>
        get() = repository.currentHistory

    override fun searchTracks(query: String): Flow<List<Track>?> {
        return repository.searchTracks(query)
    }

    override fun addTrackToHistory(track: Track) {
        repository.addTrackToHistory(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }

    override suspend fun getFavoriteTrackIds(): List<Long> {
        return repository.getFavoriteTrackIds()
    }
}
