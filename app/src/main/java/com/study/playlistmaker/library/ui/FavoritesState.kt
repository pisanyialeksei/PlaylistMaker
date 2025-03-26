package com.study.playlistmaker.library.ui

import com.study.playlistmaker.search.domain.model.Track

sealed interface FavoritesState {

    data class Content(val tracks: List<Track>) : FavoritesState

    data object Empty : FavoritesState
}
