package com.a2t.myapplication.settings.data.impl

import android.content.Context
import androidx.core.content.edit
import com.a2t.myapplication.appContext
import com.a2t.myapplication.search.data.dto.PLAYLIST_MAKER_PREFERENCES
import com.a2t.myapplication.settings.data.SettingsRepository

const val SWITCHER_KEY = "switcher_key"                                 // Ключ для состояния themeSwitcher

class SettingsRepositoryImpl: SettingsRepository {
    private val sharedPrefs = appContext.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    override fun getThemeSettings(): Boolean {
        return sharedPrefs.getBoolean(SWITCHER_KEY, false)
    }

    override fun updateThemeSetting(darkTheme: Boolean) {
        sharedPrefs.edit {
            putBoolean(SWITCHER_KEY, darkTheme)
        }
    }
}