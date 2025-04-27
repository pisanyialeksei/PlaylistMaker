package com.study.playlistmaker.library.domain.impl

import com.study.playlistmaker.library.domain.PlaylistsInteractor
import com.study.playlistmaker.library.domain.PlaylistsRepository
import com.study.playlistmaker.library.domain.model.Playlist
import com.study.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val repository: PlaylistsRepository) : PlaylistsInteractor {

    override suspend fun createPlaylist(playlist: Playlist) {
        repository.createPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun getPlaylistById(playlistId: Long): Flow<Playlist> {
        return repository.getPlaylistById(playlistId)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long): Boolean {
        return repository.addTrackToPlaylist(track, playlistId)
    }

    override suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long) {
        repository.removeTrackFromPlaylist(trackId, playlistId)
    }

    override suspend fun getTracksInPlaylist(playlistId: Long): Flow<List<Track>> {
        return repository.getTracksInPlaylist(playlistId)
    }
}
