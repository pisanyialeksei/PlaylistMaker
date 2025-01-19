package com.study.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.study.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.study.playlistmaker.player.domain.PlayerInteractor
import com.study.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.study.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.study.playlistmaker.search.data.network.RetrofitNetworkClient
import com.study.playlistmaker.search.domain.SearchInteractor
import com.study.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.study.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.study.playlistmaker.settings.domain.SettingsInteractor
import com.study.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.study.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.study.playlistmaker.sharing.domain.SharingInteractor
import com.study.playlistmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {

    private const val SHARED_PREFERENCES_NAME = "shared_preferences"
    private lateinit var sharedPreferences: SharedPreferences

    fun initPreferences(context: Context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(
            SearchRepositoryImpl(
                networkClient = RetrofitNetworkClient(),
                sharedPreferences = sharedPreferences
            )
        )
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(
            PlayerRepositoryImpl()
        )
    }

    fun provideSettingsInteractor(resources: Resources): SettingsInteractor {
        return SettingsInteractorImpl(
            SettingsRepositoryImpl(sharedPreferences, resources)
        )
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(
            context = context,
            externalNavigator = ExternalNavigatorImpl(context),
        )
    }
}
