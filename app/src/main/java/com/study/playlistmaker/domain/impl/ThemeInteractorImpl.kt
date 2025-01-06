package com.study.playlistmaker.domain.impl

import com.study.playlistmaker.domain.api.theme.ThemeInteractor
import com.study.playlistmaker.domain.api.theme.ThemeRepository

class ThemeInteractorImpl(private val repository: ThemeRepository) : ThemeInteractor {

    private var isDarkTheme = false

    init {
        isDarkTheme = repository.isDarkThemeEnabled()
    }

    override fun isDarkThemeEnabled(): Boolean {
        return isDarkTheme
    }

    override fun setDarkTheme(enabled: Boolean) {
        isDarkTheme = enabled
        repository.setDarkTheme(enabled)
    }
}
