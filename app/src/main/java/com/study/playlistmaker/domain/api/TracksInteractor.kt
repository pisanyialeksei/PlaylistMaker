package com.study.playlistmaker.domain.api

import com.study.playlistmaker.domain.models.Track

interface TracksInteractor {

    fun searchTracks(query: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?)
    }
}
