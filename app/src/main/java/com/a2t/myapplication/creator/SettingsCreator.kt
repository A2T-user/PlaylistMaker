package com.a2t.myapplication.creator

import android.app.Application
import com.a2t.myapplication.settings.data.SettingsRepository
import com.a2t.myapplication.settings.data.impl.SettingsRepositoryImpl
import com.a2t.myapplication.settings.domain.api.SettingsInteractor
import com.a2t.myapplication.settings.domain.impl.SettingsInteractorImpl
import com.a2t.myapplication.sharing.data.SharingRepository
import com.a2t.myapplication.sharing.data.impl.SharingRepositoryImpl
import com.a2t.myapplication.sharing.domain.api.SharingInteractor
import com.a2t.myapplication.sharing.domain.impl.SharingInteractorImpl

object SettingsCreator {
    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl()
    }

    private fun getSharingRepository(app: Application): SharingRepository {
        return SharingRepositoryImpl(app)
    }
    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }

    fun provideSharingInteractor(app: Application): SharingInteractor {
        return SharingInteractorImpl(getSharingRepository(app))
    }
}