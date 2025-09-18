package com.practicum.playlistmaker.settings.domain

import com.practicum.playlistmaker.settings.data.SwitchThemeRepository

class SwitchThemeInteractorImpl(private val repository : SwitchThemeRepository):
    SwitchThemeInteractor {

    override fun switchTheme(darkThemeEnabled: Boolean) {
        return repository.switchTheme(darkThemeEnabled)
    }
    override fun sharedPreferencesEdit(checked: Boolean) {
        return repository.sharedPreferencesEdit(checked)
    }
    override fun getSharedPreferencesThemeValue():Boolean {
        return repository.getSharedPreferencesThemeValue()
    }
}