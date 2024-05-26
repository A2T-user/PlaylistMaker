package com.a2t.myapplication.settings.ui.view_model

import android.app.Application
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.a2t.myapplication.creator.SettingsCreator
import com.a2t.myapplication.settings.domain.api.SettingsInteractor
import com.a2t.myapplication.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private var themeLiveData = MutableLiveData(settingsInteractor.getThemeSetting())
    private var sharingLiveData = MutableLiveData(SharingData(null,null))

    fun getThemeLiveData(): LiveData<Boolean> = themeLiveData

    fun updateThemeLiveData(darkTheme: Boolean) {
        themeLiveData.postValue(darkTheme)
        settingsInteractor.updateThemeSetting(darkTheme)
    }

    fun getDarkTheme () :Boolean = settingsInteractor.getThemeSetting()

    fun updateSharingLiveData(intentAction: String) {
        val strExtra = when (intentAction) {
            Intent.ACTION_SEND -> sharingInteractor.getShareAppLink()
            Intent.ACTION_SENDTO -> sharingInteractor.getSupportEmailData()
            else -> sharingInteractor.getTermsLink()
        }
        sharingLiveData.postValue(SharingData(intentAction, strExtra))
    }

    fun getSharingLiveData(): LiveData<SharingData> = sharingLiveData


    companion object {
        fun getViewModelFactory(app: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    SettingsCreator.provideSharingInteractor(app),
                    SettingsCreator.provideSettingsInteractor()
                )
            }
        }
    }
}