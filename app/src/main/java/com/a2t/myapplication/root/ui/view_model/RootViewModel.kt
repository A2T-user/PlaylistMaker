package com.a2t.myapplication.root.ui.view_model

import androidx.lifecycle.ViewModel
import com.a2t.myapplication.settings.domain.api.SettingsInteractor

class RootViewModel (
    private val settingsInteractor: SettingsInteractor,
) : ViewModel(){

    fun getAppTheme () : Boolean =  settingsInteractor.getThemeSetting()
}