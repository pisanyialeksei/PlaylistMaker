package com.study.playlistmaker.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.study.playlistmaker.databinding.FragmentSettingsBinding
import com.study.playlistmaker.settings.domain.model.ThemeSettings
import com.study.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsViewModel.themeState.observe(viewLifecycleOwner) { themeSettings ->
            binding.themeSwitcher.isChecked = themeSettings == ThemeSettings.DARK
        }

        setupListeners()
    }

    private fun setupListeners() {

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

    companion object {

        fun newInstance() = SettingsFragment()
    }
}
