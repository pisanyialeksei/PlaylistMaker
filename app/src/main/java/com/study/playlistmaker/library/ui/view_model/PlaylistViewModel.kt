package com.study.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.playlistmaker.library.domain.PlaylistsInteractor
import com.study.playlistmaker.library.domain.model.Playlist
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val playlistId: Long,
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    init {
        getPlaylist()
    }

    private fun getPlaylist() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylistById(playlistId)
                .collect { result ->
                    _playlist.value = result
                }
        }
    }

}
