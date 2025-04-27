package com.study.playlistmaker.library.ui.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.study.playlistmaker.library.domain.PlaylistsInteractor
import com.study.playlistmaker.library.domain.model.Playlist
import com.study.playlistmaker.library.domain.storage.PlaylistStorageService
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val playlistStorageService: PlaylistStorageService,
    private val playlistId: Long,
) : NewPlaylistViewModel(playlistsInteractor, playlistStorageService) {

    private val _playlistCover = MutableLiveData<String?>()
    val playlistCover: LiveData<String?> = _playlistCover

    private val _playlistName = MutableLiveData<String>()
    val playlistName: LiveData<String> = _playlistName

    private val _playlistDescription = MutableLiveData<String>()
    val playlistDescription: LiveData<String> = _playlistDescription

    private lateinit var currentPlaylist: Playlist

    init {
        getPlaylistData()
    }

    private fun getPlaylistData() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylistById(playlistId)
                .collect { result ->
                    currentPlaylist = result
                    _playlistCover.value = result.cover
                    _playlistName.value = result.name
                    _playlistDescription.value = result.description

                    onNameInputTextChanged(result.name)
                }
        }
    }

    fun updatePlaylist(
        name: String,
        description: String,
        coverUri: Uri?
    ) {
        viewModelScope.launch {
            val coverPath = if (coverUri != null) {
                val file = playlistStorageService.getCoverFile(name)
                playlistStorageService.saveImageToPrivateStorage(coverUri, file)
                file.absolutePath
            } else {
                currentPlaylist.cover
            }

            if (coverPath != currentPlaylist.cover && currentPlaylist.cover != null) {
                playlistStorageService.deleteCoverFile(currentPlaylist.cover!!)
            }

            val updatedPlaylist = currentPlaylist.copy(
                name = name,
                description = description,
                cover = coverPath
            )

            playlistsInteractor.updatePlaylist(updatedPlaylist)
        }
    }

}
