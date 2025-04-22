package com.study.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.playlistmaker.library.domain.PlaylistsInteractor
import com.study.playlistmaker.library.domain.model.Playlist
import com.study.playlistmaker.library.ui.PlaylistsState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
) : ViewModel() {

    private val _screenState = MutableLiveData<PlaylistsState>()
    val screenState: LiveData<PlaylistsState> = _screenState

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private var playlistsJob: Job? = null

    init {
        getPlaylists()
    }

    fun getPlaylists() {
        playlistsJob?.cancel()

        playlistsJob = viewModelScope.launch {
            playlistsInteractor
                .getPlaylists()
                .collect { result ->
                    if (result.isEmpty()) {
                        _screenState.postValue(PlaylistsState.Empty)
                    } else {
                        _screenState.postValue(PlaylistsState.Content(result))
                        _playlists.postValue(result)
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()

        playlistsJob?.cancel()
    }
}
