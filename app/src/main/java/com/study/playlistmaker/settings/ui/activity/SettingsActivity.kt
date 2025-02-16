package com.study.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.study.playlistmaker.databinding.ActivitySettingsBinding
import com.study.playlistmaker.settings.domain.model.ThemeSettings
import com.study.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsViewModel.themeState.observe(this) { themeSettings ->
            binding.themeSwitcher.isChecked = themeSettings == ThemeSettings.DARK
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            val newTheme = if (isChecked) ThemeSettings.DARK else ThemeSettings.LIGHT
            settingsViewModel.updateThemeSettings(newTheme)
        }

        binding.share.setOnClickListener {
            settingsViewModel.shareApp()
        }

        binding.support.setOnClickListener {
            settingsViewModel.openSupport()
        }

        binding.agreement.setOnClickListener {
            settingsViewModel.openTerms()
        }
    }
}
