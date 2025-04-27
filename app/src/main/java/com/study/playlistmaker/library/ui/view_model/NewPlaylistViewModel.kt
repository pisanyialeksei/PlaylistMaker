package com.study.playlistmaker.library.ui.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.playlistmaker.library.domain.PlaylistsInteractor
import com.study.playlistmaker.library.domain.model.Playlist
import com.study.playlistmaker.library.domain.storage.PlaylistStorageService
import kotlinx.coroutines.launch

open class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val playlistStorageService: PlaylistStorageService,
) : ViewModel() {

    private val _isNameValid = MutableLiveData<Boolean>(false)
    val isButtonEnabled: LiveData<Boolean> = _isNameValid

    fun onNameInputTextChanged(text: String) {
        _isNameValid.value = text.isNotBlank()
    }

    fun createPlaylist(
        name: String,
        description: String,
        coverUri: Uri?
    ) {
        viewModelScope.launch {
            val coverPath = coverUri?.let {
                val file = playlistStorageService.getCoverFile(name)
                playlistStorageService.saveImageToPrivateStorage(it, file)
                file.absolutePath
            }

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
