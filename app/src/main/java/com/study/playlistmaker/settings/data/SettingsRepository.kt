package com.study.playlistmaker.settings.data

import com.study.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsRepository {

    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}
