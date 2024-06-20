package com.a2t.myapplication.sharing.data.impl

import android.content.Context
import com.a2t.myapplication.R
import com.a2t.myapplication.sharing.data.SharingRepository

class SharingRepositoryImpl(
    private val context: Context
) : SharingRepository {
    override fun getShareAppLink(): String {
        return context.getString(R.string.practicum_android)
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.practicum_offer)
    }

    override fun getSupportEmailData(): String {
        return context.getString(R.string.e_mail)
    }
}