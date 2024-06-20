package com.a2t.myapplication.settings.data.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.a2t.myapplication.settings.data.SettingsRepository

const val SWITCHER_KEY = "switcher_key"                                 // Ключ для состояния themeSwitcher

class SettingsRepositoryImpl(
    private val sharedPrefs: SharedPreferences
): SettingsRepository {
    override fun getThemeSettings(): Boolean {
        return sharedPrefs.getBoolean(SWITCHER_KEY, false)
    }

    override fun updateThemeSetting(darkTheme: Boolean) {
        sharedPrefs.edit {
            putBoolean(SWITCHER_KEY, darkTheme)
        }
    }
}