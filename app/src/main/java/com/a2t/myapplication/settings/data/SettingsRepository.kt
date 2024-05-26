package com.a2t.myapplication.settings.data

interface SettingsRepository {
    fun getThemeSettings(): Boolean
    fun updateThemeSetting(darkTheme: Boolean)
}