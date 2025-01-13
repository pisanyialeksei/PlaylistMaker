package com.study.playlistmaker.search.data

import com.study.playlistmaker.search.domain.model.Track

interface SearchRepository {

    fun searchTracks(query: String): List<Track>?
}
