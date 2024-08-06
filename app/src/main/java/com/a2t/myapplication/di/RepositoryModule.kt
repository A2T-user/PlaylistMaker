package com.a2t.myapplication.di

import com.a2t.myapplication.mediateca.data.db.FavoritesTracksRepositoryImpl
import com.a2t.myapplication.mediateca.data.db.TrackDbConvertor
import com.a2t.myapplication.mediateca.domaim.api.FavoritesTracksRepository
import com.a2t.myapplication.player.data.PlayerRepositoryImpl
import com.a2t.myapplication.player.domain.api.PlayerRepository
import com.a2t.myapplication.search.data.dto.SearchHistory
import com.a2t.myapplication.search.data.dto.api.SearchingHistory
import com.a2t.myapplication.search.data.network.SearchRepositoryImpl
import com.a2t.myapplication.search.domain.api.SearchRepository
import com.a2t.myapplication.settings.data.SettingsRepository
import com.a2t.myapplication.settings.data.impl.SettingsRepositoryImpl
import com.a2t.myapplication.sharing.data.SharingRepository
import com.a2t.myapplication.sharing.data.impl.SharingRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    // для Search
    single<SearchRepository> {
        SearchRepositoryImpl(get(), get())
    }
    single<SearchingHistory> {
        SearchHistory(get(), get(), get())
    }

    // для Settings
    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }
    single<SharingRepository> {
        SharingRepositoryImpl(androidContext())
    }

    // для Player
    factory<PlayerRepository> {
        PlayerRepositoryImpl()
    }

    // для базы данных
    factory { TrackDbConvertor() }
    single <FavoritesTracksRepository> {
        FavoritesTracksRepositoryImpl(get(), get())
    }
}