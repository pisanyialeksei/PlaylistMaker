package com.study.playlistmaker.library.data.impl

import com.study.playlistmaker.data.db.AppDatabase
import com.study.playlistmaker.data.db.playlist.PlaylistDbConverter
import com.study.playlistmaker.data.db.track.TrackDbConverter
import com.study.playlistmaker.data.db.track.playlisttrack.PlaylistTrackEntity
import com.study.playlistmaker.library.domain.PlaylistsRepository
import com.study.playlistmaker.library.domain.model.Playlist
import com.study.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConverter: TrackDbConverter,
) : PlaylistsRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        appDatabase.playlistsDao().createPlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.playlistsDao().updatePlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlistEntities = appDatabase.playlistsDao().getPlaylists()
        val playlists = playlistEntities.map { playlistEntity ->
            val tracksCount = appDatabase.playlistTracksDao().getPlaylistTracksCount(playlistEntity.playlistId)
            playlistDbConverter.map(playlistEntity, tracksCount)
        }
        emit(playlists)
    }

    override suspend fun getPlaylistById(playlistId: Long): Flow<Playlist> = flow {
        val playlistEntity = appDatabase.playlistsDao().getPlaylistById(playlistId)
        val tracksCount = appDatabase.playlistTracksDao().getPlaylistTracksCount(playlistId)
        val playlist = playlistDbConverter.map(playlistEntity, tracksCount)
        emit(playlist)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long): Boolean {
        val trackAlreadyAdded = appDatabase.playlistTracksDao().isTrackInPlaylist(playlistId, track.trackId)
        if (trackAlreadyAdded) {
            return true
        }

        appDatabase.trackDao().insertTrack(trackDbConverter.map(track))

        appDatabase.playlistTracksDao().addTrackToPlaylist(
            PlaylistTrackEntity(
                playlistId = playlistId,
                trackId = track.trackId,
                timestamp = System.currentTimeMillis()
            )
        )

        return false
    }

    override suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long) {
        appDatabase.playlistTracksDao().removeTrackFromPlaylist(playlistId, trackId)

        val isInOtherPlaylists = appDatabase.playlistTracksDao().isTrackInAnyPlaylist(trackId)
        val isFavorite = appDatabase.favoritesDao().isTrackFavorite(trackId)
        if (!isInOtherPlaylists && !isFavorite) {
            appDatabase.trackDao().deleteTrackById(trackId)
        }
    }

    override suspend fun getTracksInPlaylist(playlistId: Long): Flow<List<Track>> = flow {
        val trackEntities = appDatabase.playlistTracksDao().getTracksInPlaylist(playlistId)
        val favoriteIds = appDatabase.favoritesDao().getFavoriteTrackIds().toSet()
        val tracks = trackEntities.map { entity ->
            trackDbConverter.map(entity, isFavorite = favoriteIds.contains(entity.trackId))
        }
        emit(tracks)
    }
}
