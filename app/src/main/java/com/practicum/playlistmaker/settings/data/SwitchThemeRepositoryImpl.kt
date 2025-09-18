package com.practicum.playlistmaker.settings.data

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.practicum.playlistmaker.creator.Creator

const val PM_PREFERENCES = "pm_preferences"
const val SWITCH_KEY = "key_for_switch"

class SwitchThemeRepositoryImpl() : SwitchThemeRepository {

    override fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    override fun sharedPreferencesEdit(checked: Boolean) {
        val sharedPref = Creator.provideSharedPreferences(PM_PREFERENCES)
        sharedPref.edit {
            putBoolean(SWITCH_KEY, checked)
        }
    }

    override fun getSharedPreferencesThemeValue():Boolean{
        val sharedPref = Creator.provideSharedPreferences(PM_PREFERENCES)
        val darkTheme = sharedPref.getBoolean(SWITCH_KEY,false)
        return darkTheme
    }
}