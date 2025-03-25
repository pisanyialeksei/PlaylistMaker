package com.study.playlistmaker.library.domain.impl

import com.study.playlistmaker.library.domain.FavoritesInteractor
import com.study.playlistmaker.library.domain.FavoritesRepository
import com.study.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val repository: FavoritesRepository
) : FavoritesInteractor {

    override suspend fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override suspend fun removeTrack(track: Track) {
        repository.removeTrack(track)
    }

    override suspend fun getAllTracks(): Flow<List<Track>> {
        return repository.getAllTracks()
    }
}
