package com.study.playlistmaker.search.data.impl

import com.study.playlistmaker.search.data.SearchRepository
import com.study.playlistmaker.search.data.dto.SearchRequest
import com.study.playlistmaker.search.data.dto.SearchResponse
import com.study.playlistmaker.search.data.network.NetworkClient
import com.study.playlistmaker.search.domain.model.Track

class SearchRepositoryImpl(private val networkClient: NetworkClient) : SearchRepository {

    override fun searchTracks(query: String): List<Track>? {
        val response = networkClient.doRequest(SearchRequest(query))
        return if (response.resultCode == 200) {
            (response as? SearchResponse)?.results?.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            } ?: emptyList()
        } else {
            null
        }
    }
}
