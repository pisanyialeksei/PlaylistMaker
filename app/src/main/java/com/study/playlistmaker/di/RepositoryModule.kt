package com.study.playlistmaker.di

import com.study.playlistmaker.data.db.playlist.PlaylistDbConverter
import com.study.playlistmaker.data.db.track.TrackDbConverter
import com.study.playlistmaker.library.data.impl.FavoritesRepositoryImpl
import com.study.playlistmaker.library.data.impl.PlaylistsRepositoryImpl
import com.study.playlistmaker.library.domain.FavoritesRepository
import com.study.playlistmaker.library.domain.PlaylistsRepository
import com.study.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.study.playlistmaker.player.domain.PlayerRepository
import com.study.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.study.playlistmaker.search.domain.SearchRepository
import com.study.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.study.playlistmaker.settings.domain.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<SearchRepository> {
        SearchRepositoryImpl(
            networkClient = get(),
            sharedPreferences = get(),
            gson = get(),
            appDatabase = get(),
        )
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(mediaPlayer = get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(sharedPreferences = get(), resources = androidContext().resources)
    }

    factory<TrackDbConverter> {
        TrackDbConverter()
    }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(
            appDatabase = get(),
            trackDbConverter = get(),
        )
    }

    factory<PlaylistDbConverter> {
        PlaylistDbConverter()
    }

    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(
            appDatabase = get(),
            playlistDbConverter = get()
        )
    }
}
