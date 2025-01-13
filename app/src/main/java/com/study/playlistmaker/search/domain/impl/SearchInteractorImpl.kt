package com.study.playlistmaker.search.domain.impl

import com.study.playlistmaker.search.data.SearchRepository
import com.study.playlistmaker.search.domain.SearchInteractor
import java.util.concurrent.Executors

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(query: String, consumer: SearchInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(query))
        }
    }
}
