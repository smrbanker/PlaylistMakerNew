package com.practicum.playlistmaker.settings.data

interface SwitchThemeRepository {
    fun switchTheme(darkThemeEnabled: Boolean)
    fun sharedPreferencesEdit(checked :Boolean)
    fun getSharedPreferencesThemeValue():Boolean
}