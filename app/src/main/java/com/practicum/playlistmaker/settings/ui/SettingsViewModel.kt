package com.practicum.playlistmaker.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator

class SettingsViewModel() : ViewModel() {
    val sharingInteractor = Creator.provideSharingInteractor()

    fun shareApp() = sharingInteractor.shareApp()
    fun openSupport() = sharingInteractor.openSupport()
    fun openTerms() = sharingInteractor.openTerms()

    val switchThemeInteractor = Creator.provideSwitchThemeInteractor()

    fun getTheme():Boolean{
        return switchThemeInteractor.getSharedPreferencesThemeValue()
    }
    fun editTheme(checked:Boolean){
        switchThemeInteractor.sharedPreferencesEdit(checked)
    }
    fun switchTheme(checked: Boolean){
        switchThemeInteractor.switchTheme(checked)
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel()
            }
        }
    }
}