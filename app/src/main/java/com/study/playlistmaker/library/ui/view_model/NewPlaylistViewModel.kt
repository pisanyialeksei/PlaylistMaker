package com.study.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.playlistmaker.library.domain.PlaylistsInteractor
import com.study.playlistmaker.library.domain.model.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
) : ViewModel() {

    private val _isNameValid = MutableLiveData<Boolean>(false)
    val isButtonEnabled: LiveData<Boolean> = _isNameValid

    fun onNameInputTextChanged(text: String) {
        _isNameValid.value = text.isNotBlank()
    }

    fun createPlaylist(
        name: String,
        description: String,
        coverPath: String?,
    ) {
        viewModelScope.launch {
            playlistsInteractor.createPlaylist(
                Playlist(
                    name = name,
                    description = description,
                    cover = coverPath,
                )
            )
        }
    }
}
