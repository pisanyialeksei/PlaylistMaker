package com.study.playlistmaker.domain.api.theme

interface ThemeRepository {

    fun isDarkThemeEnabled(): Boolean
    fun setDarkTheme(enabled: Boolean)
}
