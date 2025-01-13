package com.study.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.study.playlistmaker.settings.domain.SettingsInteractor
import com.study.playlistmaker.settings.domain.model.ThemeSettings
import com.study.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val _themeState = MutableLiveData<ThemeSettings>()
    val themeState: LiveData<ThemeSettings> = _themeState

    init {
        _themeState.value = getThemeSettings()
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun getThemeSettings(): ThemeSettings {
        return settingsInteractor.getThemeSettings()
    }

    fun updateThemeSettings(settings: ThemeSettings) {
        settingsInteractor.updateThemeSettings(settings)
        _themeState.value = settings
    }

    companion object {

        fun getViewModelFactory(
            sharingInteractor: SharingInteractor,
            settingsInteractor: SettingsInteractor,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(sharingInteractor, settingsInteractor)
            }
        }
    }
}
