package com.study.playlistmaker.library.domain

import com.study.playlistmaker.library.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {

    suspend fun createPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun getPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(playlistId: Long): Flow<Playlist>
    suspend fun addTrackToPlaylist(trackId: Long, playlistId: Long): Boolean
}
