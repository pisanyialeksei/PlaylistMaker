package com.study.playlistmaker.library.data.impl

import com.google.gson.Gson
import com.study.playlistmaker.data.db.AppDatabase
import com.study.playlistmaker.data.db.playlist.PlaylistDbConverter
import com.study.playlistmaker.library.domain.PlaylistsRepository
import com.study.playlistmaker.library.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val gson: Gson,
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

    override suspend fun addTrackToPlaylist(trackId: Long, playlistId: Long): Boolean {
        val playlists = appDatabase.playlistDao().getPlaylists()
        val playlist = playlists.find { it.playlistId == playlistId }
            ?: error("Playlist not found")

        val trackIds = if (playlist.tracks.isNullOrEmpty()) {
            mutableListOf()
        } else {
            gson.fromJson(playlist.tracks, Array<Long>::class.java).toMutableList()
        }

        val isTrackInPlaylist = trackIds.contains(trackId)

        if (!isTrackInPlaylist) {
            trackIds.add(trackId)
            val updatedPlaylist = playlist.copy(
                tracks = gson.toJson(trackIds),
                tracksCount = playlist.tracksCount + 1
            )
            appDatabase.playlistDao().updatePlaylist(updatedPlaylist)
        }

        return isTrackInPlaylist
    }
}
