package com.study.playlistmaker

import android.app.Application
import com.study.playlistmaker.creator.Creator
import com.study.playlistmaker.settings.domain.SettingsInteractor

class App : Application() {

    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()

        Creator.initPreferences(this)
        settingsInteractor = Creator.provideSettingsInteractor(resources)

        settingsInteractor.updateThemeSettings(settingsInteractor.getThemeSettings())
    }
}
