package com.study.playlistmaker.settings.domain.impl

import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.study.playlistmaker.settings.domain.SettingsRepository
import com.study.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val resources: Resources
) : SettingsRepository {

    private val themeKey = "APP_THEME"

    override fun getThemeSettings(): ThemeSettings {
        if (!sharedPreferences.contains(themeKey)) {
            sharedPreferences.edit {
                putBoolean(themeKey, isSystemInDarkTheme(resources))
            }
        }

        return if (sharedPreferences.getBoolean(themeKey, false)) {
            ThemeSettings.DARK
        } else {
            ThemeSettings.LIGHT
        }
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        if (settings == ThemeSettings.DARK) {
            sharedPreferences.edit {
                putBoolean(themeKey, true)
            }
        } else {
            sharedPreferences.edit {
                putBoolean(themeKey, false)
            }
        }

        AppCompatDelegate.setDefaultNightMode(
            if (settings == ThemeSettings.DARK) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    private fun isSystemInDarkTheme(resources: Resources): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }
}
