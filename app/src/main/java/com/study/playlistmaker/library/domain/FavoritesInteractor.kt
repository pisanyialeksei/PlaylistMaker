package com.study.playlistmaker.library.domain

import com.study.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {

    suspend fun addTrack(track: Track)
    suspend fun removeTrack(track: Track)
    suspend fun getAllTracks(): Flow<List<Track>>
}
