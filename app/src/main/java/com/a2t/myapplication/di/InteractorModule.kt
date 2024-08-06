package com.a2t.myapplication.di

import com.a2t.myapplication.mediateca.domaim.db.FavoritesTracksInteractor
import com.a2t.myapplication.mediateca.domaim.impl.FavoritesTracksInteractorImpl
import com.a2t.myapplication.player.domain.api.PlayerInteractor
import com.a2t.myapplication.player.domain.impl.PlayerInteractorImpl
import com.a2t.myapplication.search.domain.api.SearchInteractor
import com.a2t.myapplication.search.domain.impl.SearchInteractorImpl
import com.a2t.myapplication.settings.domain.api.SettingsInteractor
import com.a2t.myapplication.settings.domain.impl.SettingsInteractorImpl
import com.a2t.myapplication.sharing.domain.api.SharingInteractor
import com.a2t.myapplication.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    // для Search
    single<SearchInteractor> {
        SearchInteractorImpl(get(), get())
    }

    // для Settings
    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }
    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    // для Player
    factory<PlayerInteractor> {
        PlayerInteractorImpl(get(), get(), get())
    }
    //для базы данных
    factory<FavoritesTracksInteractor> {
        FavoritesTracksInteractorImpl(get())
    }



}