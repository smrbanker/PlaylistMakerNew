package com.practicum.playlistmaker.settings.domain

interface SwitchThemeInteractor {
    fun switchTheme(darkThemeEnabled : Boolean)
    fun sharedPreferencesEdit(checked : Boolean)
    fun getSharedPreferencesThemeValue() : Boolean
}