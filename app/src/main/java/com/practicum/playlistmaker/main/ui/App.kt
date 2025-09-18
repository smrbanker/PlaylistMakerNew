package com.practicum.playlistmaker.main.ui

import android.app.Application
import com.practicum.playlistmaker.creator.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Creator.initCreatorApplication(this)

        val sharedPreferencesInteractor = Creator.provideSwitchThemeInteractor()
        val darkTheme = Creator.provideSwitchThemeRepository().getSharedPreferencesThemeValue()
        sharedPreferencesInteractor.switchTheme(darkTheme)
    }
}