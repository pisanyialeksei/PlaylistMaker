package com.study.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.playlistmaker.library.domain.FavoritesInteractor
import com.study.playlistmaker.library.ui.FavoritesState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor,
) : ViewModel() {

    private val _screenState = MutableLiveData<FavoritesState>()
    val screenState: LiveData<FavoritesState> = _screenState

    private var favoritesJob: Job? = null

    init {
        getFavorites()
    }

    fun getFavorites() {
        favoritesJob?.cancel()

        favoritesJob = viewModelScope.launch {
            favoritesInteractor
                .getAllTracks()
                .collect { tracks ->
                    when {
                        tracks.isNotEmpty() -> _screenState.postValue(FavoritesState.Content(tracks))
                        else -> _screenState.postValue(FavoritesState.Empty)
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()

        favoritesJob?.cancel()
    }
}
