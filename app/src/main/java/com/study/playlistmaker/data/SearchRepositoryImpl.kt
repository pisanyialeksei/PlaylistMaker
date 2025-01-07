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
                it.toModel()
            } ?: emptyList()
        } else {
            null
        }
    }
}
