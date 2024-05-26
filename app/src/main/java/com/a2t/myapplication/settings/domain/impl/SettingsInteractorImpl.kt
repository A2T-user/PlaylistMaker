package com.a2t.myapplication.settings.domain.impl

import com.a2t.myapplication.settings.data.SettingsRepository
import com.a2t.myapplication.settings.domain.api.SettingsInteractor

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository
): SettingsInteractor {

    override fun getThemeSetting(): Boolean {
        return settingsRepository.getThemeSettings()
    }

    override fun updateThemeSetting(darkTheme: Boolean) {
        settingsRepository.updateThemeSetting(darkTheme)
    }
}