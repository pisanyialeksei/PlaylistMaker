package com.study.playlistmaker.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.study.playlistmaker.App
import com.study.playlistmaker.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var appContext: App
    private lateinit var themeSwitch: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        appContext = applicationContext as App
        themeSwitch = findViewById(R.id.themeSwitcher)
        themeSwitch.isChecked = appContext.themeInteractor.isDarkThemeEnabled()

        setupListeners()
    }

    private fun setupListeners() {
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            appContext.switchTheme(isChecked)
        }

        findViewById<Button>(R.id.share).setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
            }
            startActivity(shareIntent)
        }

        findViewById<Button>(R.id.support).setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_mail_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_mail_body))
            }
            startActivity(supportIntent)
        }

        findViewById<Button>(R.id.agreement).setOnClickListener {
            val agreementIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.agreement_link))
            }
            startActivity(agreementIntent)
        }
    }
}
