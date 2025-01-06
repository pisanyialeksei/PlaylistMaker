package com.study.playlistmaker.domain.api.search

import com.study.playlistmaker.domain.models.Track

interface SearchRepository {

    fun searchTracks(query: String): List<Track>?
}
