package com.study.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.room.Room
import com.google.gson.Gson
import com.study.playlistmaker.data.db.AppDatabase
import com.study.playlistmaker.library.data.impl.PlaylistStorageServiceImpl
import com.study.playlistmaker.library.domain.storage.PlaylistStorageService
import com.study.playlistmaker.search.data.network.ItunesSearchApiService
import com.study.playlistmaker.search.data.network.NetworkClient
import com.study.playlistmaker.search.data.network.RetrofitNetworkClient
import com.study.playlistmaker.sharing.data.ExternalNavigator
import com.study.playlistmaker.sharing.data.StringProvider
import com.study.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.study.playlistmaker.sharing.data.impl.StringProviderImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://itunes.apple.com"
private const val SHARED_PREFERENCES_NAME = "shared_preferences"

val dataModule = module {

    single<ItunesSearchApiService> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesSearchApiService::class.java)
    }

    single<SharedPreferences> {
        androidContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(itunesSearchApiService = get())
    }

    single<StringProvider> {
        StringProviderImpl(androidContext())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    factory<MediaPlayer> {
        MediaPlayer()
    }

    single<Gson> {
        Gson()
    }

    single<AppDatabase> {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }

    single<PlaylistStorageService> {
        PlaylistStorageServiceImpl(androidContext())
    }
}
