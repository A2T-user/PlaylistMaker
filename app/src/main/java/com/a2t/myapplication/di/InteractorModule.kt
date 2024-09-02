package com.a2t.myapplication.di

import com.a2t.myapplication.mediateca.domain.db.FavoritesTracksInteractor
import com.a2t.myapplication.mediateca.domain.db.PlaylistInteractor
import com.a2t.myapplication.mediateca.domain.impl.FavoritesTracksInteractorImpl
import com.a2t.myapplication.mediateca.domain.impl.PlaylistInteractorImpl
import com.a2t.myapplication.player.domain.api.PlayerInteractor
import com.a2t.myapplication.player.domain.impl.PlayerInteractorImpl
import com.a2t.myapplication.search.domain.api.SearchInteractor
import com.a2t.myapplication.search.domain.impl.SearchInteractorImpl
import com.a2t.myapplication.settings.domain.api.SettingsInteractor
import com.a2t.myapplication.settings.domain.impl.SettingsInteractorImpl
import com.a2t.myapplication.sharing.domain.api.SharingInteractor
import com.a2t.myapplication.sharing.domain.impl.SharingInteractorImpl
import com.a2t.myapplication.showplaylist.domain.db.ShowPlaylistInteractor
import com.a2t.myapplication.showplaylist.domain.impl.ShowPlaylistInteractorImpl
import com.a2t.myapplication.сreateplaylist.domain.api.CreatePlaylistInteractor
import com.a2t.myapplication.сreateplaylist.domain.impl.CreatePlaylistInteractorImpl
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
        PlayerInteractorImpl(get(), get())
    }
    //для базы данных
    factory<FavoritesTracksInteractor> {
        FavoritesTracksInteractorImpl(get())
    }

    // для CreatePlayList
    single<CreatePlaylistInteractor> {
        CreatePlaylistInteractorImpl(get(), get())
    }

    // для PlayList
    single<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }

    // для ShowPlayList
    single<ShowPlaylistInteractor> {
        ShowPlaylistInteractorImpl(get())
    }
}