package com.study.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.study.playlistmaker.domain.api.theme.ThemeInteractor

class App : Application() {

    lateinit var themeInteractor: ThemeInteractor
        private set

    override fun onCreate() {
        super.onCreate()

        Creator.initPreferences(this)
        themeInteractor = Creator.provideThemeInteractor(resources)
        updateUITheme()
    }

    fun switchTheme(toDarkTheme: Boolean) {
        themeInteractor.setDarkTheme(toDarkTheme)
        updateUITheme()
    }

    private fun updateUITheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (themeInteractor.isDarkThemeEnabled()) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
