package com.a2t.myapplication.sharing.domain.api

interface SharingInteractor {
    fun getShareAppLink(): String
    fun getTermsLink(): String
    fun getSupportEmailData(): String
}