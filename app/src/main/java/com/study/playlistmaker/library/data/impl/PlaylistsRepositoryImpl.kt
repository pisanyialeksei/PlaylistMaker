package com.study.playlistmaker.library.data.impl

import com.study.playlistmaker.data.db.AppDatabase
import com.study.playlistmaker.data.db.playlist.PlaylistDbConverter
import com.study.playlistmaker.library.domain.PlaylistsRepository
import com.study.playlistmaker.library.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
) : PlaylistsRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().createPlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().updatePlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlistEntities = appDatabase.playlistDao().getPlaylists()
        val playlists = playlistEntities.map { playlistEntity ->
            playlistDbConverter.map(playlistEntity)
        }
        emit(playlists)
    }
}
