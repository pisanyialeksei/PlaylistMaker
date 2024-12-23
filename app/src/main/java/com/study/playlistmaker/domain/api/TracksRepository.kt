package com.study.playlistmaker.domain.api

import com.study.playlistmaker.domain.models.Track

interface TracksRepository {

    fun searchTracks(query: String): List<Track>?
}
