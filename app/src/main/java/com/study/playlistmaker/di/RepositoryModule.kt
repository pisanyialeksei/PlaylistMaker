package com.study.playlistmaker.di

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
        SearchRepositoryImpl(networkClient = get(), sharedPreferences = get())
    }

    single<PlayerRepository> {
        PlayerRepositoryImpl()
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(sharedPreferences = get(), resources = androidContext().resources)
    }
}
