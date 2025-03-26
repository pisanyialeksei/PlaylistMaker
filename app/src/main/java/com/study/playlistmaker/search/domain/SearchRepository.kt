package com.study.playlistmaker.search.domain

import com.study.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface SearchRepository {

    val currentHistory: List<Track>

    fun searchTracks(query: String): Flow<List<Track>?>
    fun addTrackToHistory(track: Track)
    fun clearHistory()
    suspend fun getFavoriteTrackIds(): List<Long>
}
