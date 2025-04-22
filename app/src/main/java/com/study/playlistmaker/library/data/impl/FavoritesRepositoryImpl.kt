package com.study.playlistmaker.library.data.impl

import com.study.playlistmaker.data.db.AppDatabase
import com.study.playlistmaker.data.db.track.TrackDbConverter
import com.study.playlistmaker.library.domain.FavoritesRepository
import com.study.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
) : FavoritesRepository {

    override suspend fun addTrack(track: Track) {
        appDatabase.trackDao().insertTrack(trackDbConverter.map(track))
    }

    override suspend fun removeTrack(track: Track) {
        appDatabase.trackDao().deleteTrack(trackDbConverter.map(track))
    }

    override suspend fun getAllTracks(): Flow<List<Track>> = flow {
        val trackEntities = appDatabase.trackDao().getAllTracks()
        val tracks = trackEntities.map { trackEntity ->
            trackDbConverter.map(trackEntity)
        }
        emit(tracks)
    }
}
