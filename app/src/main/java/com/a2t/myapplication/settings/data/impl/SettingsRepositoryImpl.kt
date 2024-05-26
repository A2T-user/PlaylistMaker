package com.a2t.myapplication.settings.data.impl

import com.a2t.myapplication.main.ui.sharedPrefs
import com.a2t.myapplication.settings.data.SettingsRepository

const val SWITCHER_KEY = "switcher_key"                                 // Ключ для состояния themeSwitcher

class SettingsRepositoryImpl: SettingsRepository {
    override fun getThemeSettings(): Boolean {
        return sharedPrefs.getBoolean(SWITCHER_KEY, false)
    }

    override fun updateThemeSetting(darkTheme: Boolean) {
        sharedPrefs.edit()
            .putBoolean(SWITCHER_KEY, darkTheme)
            .apply()
    }
}