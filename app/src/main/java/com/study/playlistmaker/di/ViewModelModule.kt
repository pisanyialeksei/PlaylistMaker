package com.study.playlistmaker.di

import com.study.playlistmaker.library.ui.view_model.FavoritesViewModel
import com.study.playlistmaker.library.ui.view_model.NewPlaylistViewModel
import com.study.playlistmaker.library.ui.view_model.PlaylistsViewModel
import com.study.playlistmaker.player.ui.model.PlayerTrack
import com.study.playlistmaker.player.ui.view_model.PlayerViewModel
import com.study.playlistmaker.search.ui.view_model.SearchViewModel
import com.study.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { (track: PlayerTrack) ->
        PlayerViewModel(
            playerInteractor = get(),
            favoritesInteractor = get(),
            playlistsInteractor = get(),
            track = track,
        )
    }

    viewModel {
        SearchViewModel(searchInteractor = get())
    }

    viewModel {
        SettingsViewModel(sharingInteractor = get(), settingsInteractor = get())
    }

    viewModel {
        FavoritesViewModel(favoritesInteractor = get())
    }

    viewModel {
        PlaylistsViewModel(playlistsInteractor = get())
    }

    viewModel {
        NewPlaylistViewModel(playlistsInteractor = get())
    }
}
