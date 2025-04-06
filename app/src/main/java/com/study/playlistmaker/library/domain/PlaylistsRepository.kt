package com.study.playlistmaker.library.domain

import com.study.playlistmaker.library.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {

    suspend fun createPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun getPlaylists(): Flow<List<Playlist>>
}
