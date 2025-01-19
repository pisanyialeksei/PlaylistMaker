package com.study.playlistmaker.search.domain.impl

import com.study.playlistmaker.search.data.SearchRepository
import com.study.playlistmaker.search.domain.SearchInteractor
import com.study.playlistmaker.search.domain.model.Track
import java.util.concurrent.Executors

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override val currentHistory: MutableList<Track>
        get() = repository.currentHistory

    override fun searchTracks(query: String, consumer: SearchInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(query))
        }
    }

    override fun addTrackToHistory(track: Track) {
        repository.addTrackToHistory(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}
