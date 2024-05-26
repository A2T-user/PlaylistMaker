package com.a2t.myapplication.sharing.domain.impl

import com.a2t.myapplication.sharing.data.SharingRepository
import com.a2t.myapplication.sharing.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val saringRepository: SharingRepository
) : SharingInteractor {
    // Получить ссылку на приложение
    override fun getShareAppLink(): String {
        return saringRepository.getShareAppLink()
    }

    // Получить ссылку на условия
    override fun getTermsLink(): String {

        return saringRepository.getTermsLink()
    }

    // Получить данные электронной почты службы поддержки
    override fun getSupportEmailData(): String {
        return saringRepository.getSupportEmailData()
    }
}