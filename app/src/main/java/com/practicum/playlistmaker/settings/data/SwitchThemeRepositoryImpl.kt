package com.practicum.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

const val SWITCH_KEY = "key_for_switch"

class SwitchThemeRepositoryImpl(private val sharedPref : SharedPreferences) : SwitchThemeRepository {

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
        sharedPref.edit {
            putBoolean(SWITCH_KEY, checked)
        }
    }

    override fun getSharedPreferencesThemeValue():Boolean{
        val darkTheme = sharedPref.getBoolean(SWITCH_KEY,false)
        return darkTheme
    }
}