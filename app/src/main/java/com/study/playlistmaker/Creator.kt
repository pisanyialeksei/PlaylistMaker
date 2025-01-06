package com.study.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.study.playlistmaker.data.HistoryRepositoryImpl
import com.study.playlistmaker.data.SearchRepositoryImpl
import com.study.playlistmaker.data.ThemeRepositoryImpl
import com.study.playlistmaker.data.network.RetrofitNetworkClient
import com.study.playlistmaker.domain.api.history.HistoryInteractor
import com.study.playlistmaker.domain.api.search.SearchInteractor
import com.study.playlistmaker.domain.api.search.SearchRepository
import com.study.playlistmaker.domain.api.theme.ThemeInteractor
import com.study.playlistmaker.domain.impl.HistoryInteractorImpl
import com.study.playlistmaker.domain.impl.SearchInteractorImpl
import com.study.playlistmaker.domain.impl.ThemeInteractorImpl

object Creator {

    private const val SHARED_PREFERENCES_NAME = "shared_preferences"
    private lateinit var sharedPreferences: SharedPreferences

    fun initPreferences(context: Context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    private fun getSearchRepository(): SearchRepository {
        return SearchRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(getSearchRepository())
    }

    fun provideHistoryInteractor(): HistoryInteractor {
        return HistoryInteractorImpl(
            HistoryRepositoryImpl(sharedPreferences)
        )
    }

    fun provideThemeInteractor(resources: Resources): ThemeInteractor {
        return ThemeInteractorImpl(
            ThemeRepositoryImpl(sharedPreferences, resources)
        )
    }
}
