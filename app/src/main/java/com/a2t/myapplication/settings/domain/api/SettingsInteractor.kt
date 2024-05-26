package com.a2t.myapplication.settings.domain.api

interface SettingsInteractor {
    fun getThemeSetting(): Boolean
    fun updateThemeSetting(darkTheme: Boolean)
}