package com.study.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

const val SHARED_PREFERENCES = "shared_preferences"
const val DARK_THEME_KEY = "dark_theme"

class App : Application() {

    var isDarkThemeEnabled = false

    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)

        if (!sharedPreferences.contains(DARK_THEME_KEY)) {
            isDarkThemeEnabled = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

            sharedPreferences.edit {
                putBoolean(DARK_THEME_KEY, isDarkThemeEnabled)
            }
        } else {
            isDarkThemeEnabled = sharedPreferences.getBoolean(DARK_THEME_KEY, false)
        }

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun switchTheme(toDarkTheme: Boolean) {
        isDarkThemeEnabled = toDarkTheme
        AppCompatDelegate.setDefaultNightMode(
            if (toDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
