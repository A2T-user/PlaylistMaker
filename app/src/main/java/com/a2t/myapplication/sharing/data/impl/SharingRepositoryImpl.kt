package com.a2t.myapplication.sharing.data.impl

import android.app.Application
import com.a2t.myapplication.R
import com.a2t.myapplication.sharing.data.SharingRepository

class SharingRepositoryImpl(
    private val app: Application
) : SharingRepository {
    override fun getShareAppLink(): String {
        return app.getString(R.string.practicum_android)
    }

    override fun getTermsLink(): String {
        return app.getString(R.string.practicum_offer)
    }

    override fun getSupportEmailData(): String {
        return app.getString(R.string.e_mail)
    }
}