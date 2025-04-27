package com.study.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.playlistmaker.library.domain.PlaylistsInteractor
import com.study.playlistmaker.library.domain.model.Playlist
import com.study.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val playlistId: Long,
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    private val _playlistTracks = MutableLiveData<List<Track>>()
    val playlistTracks: LiveData<List<Track>> = _playlistTracks

    private val _playlistDuration = MutableLiveData<Int>()
    val playlistDuration: LiveData<Int> = _playlistDuration

    private val _playlistDeleted = MutableLiveData<Boolean>()
    val playlistDeleted: LiveData<Boolean> = _playlistDeleted

    init {
        getPlaylist()
        getPlaylistTracks()
    }

    fun getPlaylist() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylistById(playlistId)
                .collect { result ->
                    _playlist.value = result
                }
        }
    }

    fun getPlaylistTracks() {
        viewModelScope.launch {
            playlistsInteractor.getTracksInPlaylist(playlistId)
                .collect { tracks ->
                    _playlistTracks.value = tracks
                    setPlaylistDuration(tracks)
                }
        }
    }

    fun removeTrackFromPlaylist(track: Track) {
        viewModelScope.launch {
            playlistsInteractor.removeTrackFromPlaylist(track.trackId, playlistId)
            getPlaylist()
            getPlaylistTracks()
        }
    }

    fun sharePlaylist(text: String) {
        playlistsInteractor.sharePlaylist(text)
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistsInteractor.deletePlaylistById(playlistId)
            _playlistDeleted.postValue(true)
        }
    }

    private fun setPlaylistDuration(tracks: List<Track>) {
        val totalMillis = tracks.sumOf { it.trackTimeMillis }
        _playlistDuration.value = SimpleDateFormat("mm", Locale.getDefault()).format(totalMillis).toInt()
    }
}
