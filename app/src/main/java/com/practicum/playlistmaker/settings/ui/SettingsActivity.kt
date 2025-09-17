package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.R

class SettingsActivity : AppCompatActivity() {

    private val viewModel by viewModels<SettingsViewModel> { SettingsViewModel.getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets }

        val image4 = findViewById<ImageView>(R.id.button_back) //кнопка назад
        image4.setOnClickListener { finish() }

        val shareButton = findViewById<ImageView>(R.id.share_button) //кнопка поделиться
        shareButton.setOnClickListener { viewModel.shareApp() }

        val supportButton = findViewById<ImageView>(R.id.support_button) //кнопка поддержка
        supportButton.setOnClickListener { viewModel.openSupport() }

        val agreementButton = findViewById<ImageView>(R.id.agreement_button) //кнопка соглашение
        agreementButton.setOnClickListener { viewModel.openTerms() }

        val themeSwitcher = findViewById<Switch>(R.id.themeSwitcher)

        themeSwitcher.isChecked = viewModel.getTheme()
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.editTheme(checked)
            viewModel.switchTheme(checked)
        }
    }
}