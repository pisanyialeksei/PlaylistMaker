package com.study.playlistmaker.settings.domain.impl

import com.study.playlistmaker.settings.data.SettingsRepository
import com.study.playlistmaker.settings.domain.SettingsInteractor
import com.study.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl(
    private val repository: SettingsRepository
) : SettingsInteractor {

    override fun getThemeSettings(): ThemeSettings {
        return repository.getThemeSettings()
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        repository.updateThemeSetting(settings)
    }
}
