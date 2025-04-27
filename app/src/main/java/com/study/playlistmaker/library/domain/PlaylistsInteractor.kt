package com.study.playlistmaker.library.domain

import com.study.playlistmaker.library.domain.model.Playlist
import com.study.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {

    suspend fun createPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun getPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(playlistId: Long): Flow<Playlist>
    suspend fun addTrackToPlaylist(track: Track, playlistId: Long): Boolean
    suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long)
    suspend fun getTracksInPlaylist(playlistId: Long): Flow<List<Track>>
}
