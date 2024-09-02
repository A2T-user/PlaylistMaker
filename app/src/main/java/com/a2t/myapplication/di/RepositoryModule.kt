package com.a2t.myapplication.di

import com.a2t.myapplication.mediateca.data.db.FavoritesTracksRepositoryImpl
import com.a2t.myapplication.mediateca.data.db.PlaylistRepositoryImpl
import com.a2t.myapplication.mediateca.data.db.TrackDbConvertor
import com.a2t.myapplication.mediateca.domain.api.FavoritesTracksRepository
import com.a2t.myapplication.mediateca.domain.api.PlaylistRepository
import com.a2t.myapplication.search.domain.api.SearchHistoryRepository
import com.a2t.myapplication.search.data.network.SearchHistoryRepositoryImpl
import com.a2t.myapplication.search.data.network.SearchRepositoryImpl
import com.a2t.myapplication.search.domain.api.SearchRepository
import com.a2t.myapplication.settings.data.SettingsRepository
import com.a2t.myapplication.settings.data.impl.SettingsRepositoryImpl
import com.a2t.myapplication.sharing.data.SharingRepository
import com.a2t.myapplication.sharing.data.impl.SharingRepositoryImpl
import com.a2t.myapplication.сreateplaylist.data.db.CreatePlaylistRepositoryImpl
import com.a2t.myapplication.сreateplaylist.domain.db.CreatePlaylistRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    // для Search
    single<SearchRepository> {
        SearchRepositoryImpl(get(), get(), get())
    }
    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }

    // для Settings
    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }
    single<SharingRepository> {
        SharingRepositoryImpl(androidContext())
    }

    // для базы данных
    factory { TrackDbConvertor() }
    single <FavoritesTracksRepository> {
        FavoritesTracksRepositoryImpl(get(), get())
    }

    // для CreatePlayList
    single<CreatePlaylistRepository> {
        CreatePlaylistRepositoryImpl(androidContext(), get())
    }

    // для PlayList
    single<PlaylistRepository> {
        PlaylistRepositoryImpl( get(), get())
    }
}