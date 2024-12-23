package com.study.playlistmaker.data

import com.study.playlistmaker.data.dto.SearchRequest
import com.study.playlistmaker.data.dto.SearchResponse
import com.study.playlistmaker.domain.api.TracksRepository
import com.study.playlistmaker.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(query: String): List<Track>? {
        val response = networkClient.doRequest(SearchRequest(query))
        if (response.resultCode == 200) {
            return (response as? SearchResponse)?.results?.map {
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
            return null
        }
    }
}
