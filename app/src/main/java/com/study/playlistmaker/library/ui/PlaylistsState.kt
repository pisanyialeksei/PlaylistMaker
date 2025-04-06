package com.study.playlistmaker.library.ui

import com.study.playlistmaker.library.domain.model.Playlist

sealed interface PlaylistsState {

    data class Content(val playlists: List<Playlist>) : PlaylistsState

    data object Empty : PlaylistsState
}
