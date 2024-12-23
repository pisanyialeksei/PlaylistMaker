package com.study.playlistmaker.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import com.google.android.material.switchmaterial.SwitchMaterial
import com.study.playlistmaker.App
import com.study.playlistmaker.DARK_THEME_KEY
import com.study.playlistmaker.R
import com.study.playlistmaker.SHARED_PREFERENCES

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val appContext = applicationContext as App
        themeSwitcher.isChecked = appContext.isDarkThemeEnabled
        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            appContext.switchTheme(isChecked)

            val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
            sharedPreferences.edit {
                putBoolean(DARK_THEME_KEY, isChecked)
            }
        }

        val shareButton = findViewById<Button>(R.id.share)
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
            }
            startActivity(shareIntent)
        }

        val supportButton = findViewById<Button>(R.id.support)
        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_mail_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_mail_body))
            }
            startActivity(supportIntent)
        }

        val agreementButton = findViewById<Button>(R.id.agreement)
        agreementButton.setOnClickListener {
            val agreementIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.agreement_link))
            }
            startActivity(agreementIntent)
        }
    }
}
