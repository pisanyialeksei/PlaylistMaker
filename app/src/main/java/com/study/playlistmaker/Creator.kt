package com.study.playlistmaker

import android.content.SharedPreferences
import com.study.playlistmaker.data.HistoryRepositoryImpl
import com.study.playlistmaker.data.SearchRepositoryImpl
import com.study.playlistmaker.data.network.RetrofitNetworkClient
import com.study.playlistmaker.domain.api.history.HistoryInteractor
import com.study.playlistmaker.domain.api.search.SearchInteractor
import com.study.playlistmaker.domain.api.search.SearchRepository
import com.study.playlistmaker.domain.impl.HistoryInteractorImpl
import com.study.playlistmaker.domain.impl.SearchInteractorImpl

object Creator {

    private fun getSearchRepository(): SearchRepository {
        return SearchRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(getSearchRepository())
    }

    fun provideHistoryInteractor(sharedPreferences: SharedPreferences): HistoryInteractor {
        return HistoryInteractorImpl(
            HistoryRepositoryImpl(sharedPreferences)
        )
    }
}
