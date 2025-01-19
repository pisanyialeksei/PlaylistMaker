package com.study.playlistmaker.search.ui.model

import com.study.playlistmaker.search.domain.model.Track

sealed class SearchState {

    data object Loading : SearchState()
    data object Empty : SearchState()
    sealed class Error : SearchState() {
        data object Network : Error()
        data object EmptyResult : Error()
    }

    data class Content(val tracks: List<Track>) : SearchState()
    data class History(val tracks: List<Track>) : SearchState()
}
