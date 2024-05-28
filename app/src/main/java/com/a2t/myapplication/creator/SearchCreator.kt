package com.a2t.myapplication.creator

import com.a2t.myapplication.search.data.dto.SearchHistory
import com.a2t.myapplication.search.data.network.RetrofitNetworkClient
import com.a2t.myapplication.search.data.network.SearchRepositoryImpl
import com.a2t.myapplication.search.domain.api.SearchInteractor
import com.a2t.myapplication.search.domain.api.SearchRepository
import com.a2t.myapplication.search.domain.impl.SearchInteractorImpl

object SearchCreator {
    private fun getSearchRepository(): SearchRepository {
        return SearchRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(getSearchRepository(), SearchHistory())
    }
}