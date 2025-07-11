package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val PM_PREFERENCES = "pm_preferences"
const val SAVE_LIST = "save_list"
const val SWITCH_KEY = "key_for_switch"
const val SAVE_KEY = "key_for_save"

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val sharedPrefs = getSharedPreferences(PM_PREFERENCES, MODE_PRIVATE)

        darkTheme = sharedPrefs.getBoolean(SWITCH_KEY, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}