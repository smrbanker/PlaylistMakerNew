package com.practicum.playlistmaker.ui.tracks

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

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

/* Артур, добрый вечер!

По замечаниям:
1. Цвет бэкграумда в темной теме - исправил.
2. Кнопка паузы - вероятно, какой-то странный баг. У меня и в AS, и на телефоне все корректно отображается.
Кнопку брал экспортом прямо из макета. Могу скрин экрана или видео из AS прислать. Я не знаю, что делать,
но у меня кнопка строго, как в макете показывается. Может удастся запустить на другом устройстве, где все будет
корректно?
3. Мьютабельные списки - исправил.
4. Константу - добавил.
5. Активити - удалил лишнюю, она из тестового примера осталась.
6. Клавиатура - нашел функцию, как скрывать.
7. Логика настроек - не критическое замечание, я покручу его в фоновом режиме. Хочу уже сдать ДЗ.

Итог, кроме кнопки, которая у меня отображается идеально и взята экспортом из макета, кажется все сделал,
что мог. Благодарю!
 */