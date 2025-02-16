package com.study.playlistmaker

import android.app.Application
import com.study.playlistmaker.di.dataModule
import com.study.playlistmaker.di.interactorModule
import com.study.playlistmaker.di.repositoryModule
import com.study.playlistmaker.di.viewModelModule
import com.study.playlistmaker.settings.domain.SettingsInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    private val settingsInteractor: SettingsInteractor by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                interactorModule,
                repositoryModule,
                viewModelModule,
            )
        }

        settingsInteractor.updateThemeSettings(settingsInteractor.getThemeSettings())
    }
}
