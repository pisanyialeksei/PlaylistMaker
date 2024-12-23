package com.study.playlistmaker

import com.study.playlistmaker.data.TracksRepositoryImpl
import com.study.playlistmaker.data.network.RetrofitNetworkClient
import com.study.playlistmaker.domain.api.TracksInteractor
import com.study.playlistmaker.domain.api.TracksRepository
import com.study.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }
}
