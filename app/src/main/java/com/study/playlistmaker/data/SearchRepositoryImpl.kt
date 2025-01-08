package com.study.playlistmaker.data

import com.study.playlistmaker.data.dto.SearchRequest
import com.study.playlistmaker.data.dto.SearchResponse
import com.study.playlistmaker.domain.api.search.SearchRepository
import com.study.playlistmaker.domain.models.Track

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
