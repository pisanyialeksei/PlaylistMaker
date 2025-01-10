package com.study.playlistmaker.domain.api.search

import com.study.playlistmaker.domain.models.Track

interface SearchInteractor {

    fun searchTracks(query: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?)
    }
}
