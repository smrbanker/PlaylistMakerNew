package com.practicum.playlistmaker.main.ui

import android.app.Application
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.settings.data.SwitchThemeRepository
import com.practicum.playlistmaker.settings.domain.SwitchThemeInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

        val sharedPreferencesInteractor : SwitchThemeInteractor by inject()
        val darkThemeAll : SwitchThemeRepository by inject()
        val darkTheme = darkThemeAll.getSharedPreferencesThemeValue()
        sharedPreferencesInteractor.switchTheme(darkTheme)
    }
}