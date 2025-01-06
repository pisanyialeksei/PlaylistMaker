package com.study.playlistmaker.domain.api.theme

interface ThemeInteractor {

    fun isDarkThemeEnabled(): Boolean
    fun setDarkTheme(enabled: Boolean)
}
