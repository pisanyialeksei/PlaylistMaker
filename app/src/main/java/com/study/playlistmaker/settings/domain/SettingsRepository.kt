package com.study.playlistmaker.settings.domain

import com.study.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsRepository {

    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}
