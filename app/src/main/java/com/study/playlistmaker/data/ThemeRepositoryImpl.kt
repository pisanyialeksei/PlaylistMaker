package com.study.playlistmaker.data

import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import androidx.core.content.edit
import com.study.playlistmaker.domain.api.theme.ThemeRepository

class ThemeRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val resources: Resources
) : ThemeRepository {

    private val darkThemeKey = "DARK_THEME"

    override fun isDarkThemeEnabled(): Boolean {
        if (!sharedPreferences.contains(darkThemeKey)) {
            sharedPreferences.edit {
                putBoolean(darkThemeKey, isSystemInDarkTheme(resources))
            }
        }

        return sharedPreferences.getBoolean(darkThemeKey, false)
    }

    override fun setDarkTheme(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(darkThemeKey, enabled)
        }
    }

    private fun isSystemInDarkTheme(resources: Resources): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }
}
