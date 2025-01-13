package com.study.playlistmaker.search.domain

import com.study.playlistmaker.search.domain.model.Track

interface SearchInteractor {

    fun searchTracks(query: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?)
    }
}
