package com.study.playlistmaker.library.data.impl

import com.study.playlistmaker.data.db.AppDatabase
import com.study.playlistmaker.data.db.track.TrackDbConverter
import com.study.playlistmaker.data.db.track.favorite.FavoriteEntity
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

        appDatabase.favoritesDao().addToFavorites(
            FavoriteEntity(
                trackId = track.trackId,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override suspend fun removeTrack(track: Track) {
        appDatabase.favoritesDao().removeFromFavorites(track.trackId)

        val isInAnyPlaylist = appDatabase.playlistTracksDao().isTrackInAnyPlaylist(track.trackId)
        if (!isInAnyPlaylist) {
            appDatabase.trackDao().deleteTrack(trackDbConverter.map(track))
        }
    }

    override suspend fun getAllTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.favoritesDao().getFavoriteTracks().map { entity ->
            trackDbConverter.map(entity, isFavorite = true)
        }
        emit(tracks)
    }
}
