package com.study.playlistmaker.di

import com.study.playlistmaker.library.domain.FavoritesInteractor
import com.study.playlistmaker.library.domain.impl.FavoritesInteractorImpl
import com.study.playlistmaker.player.domain.PlayerInteractor
import com.study.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.study.playlistmaker.search.domain.SearchInteractor
import com.study.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.study.playlistmaker.settings.domain.SettingsInteractor
import com.study.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.study.playlistmaker.sharing.domain.SharingInteractor
import com.study.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<SearchInteractor> {
        SearchInteractorImpl(repository = get())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(repository = get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(repository = get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(stringProvider = get(), externalNavigator = get())
    }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(repository = get())
    }
}
