package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shareButton = binding.shareButton //кнопка поделиться
        shareButton.setOnClickListener { viewModel.shareApp() }

        val supportButton = binding.supportButton //кнопка поддержка
        supportButton.setOnClickListener { viewModel.openSupport() }

        val agreementButton = binding.agreementButton //кнопка соглашение
        agreementButton.setOnClickListener { viewModel.openTerms() }

        val themeSwitcher = binding.themeSwitcher

        themeSwitcher.isChecked = viewModel.getTheme()
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.editTheme(checked)
            viewModel.switchTheme(checked)
        }
    }
}