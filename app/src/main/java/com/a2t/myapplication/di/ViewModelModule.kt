package com.a2t.myapplication.di

import com.a2t.myapplication.mediateca.ui.view_model.FavoritesViewModel
import com.a2t.myapplication.mediateca.ui.view_model.PlaylistViewModel
import com.a2t.myapplication.player.ui.view_model.PlayerViewModel
import com.a2t.myapplication.root.ui.view_model.RootViewModel
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.search.ui.view_model.SearchViewModel
import com.a2t.myapplication.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    // для Root
    viewModel {
        RootViewModel(get())
    }

    // для Search
    viewModel {
        SearchViewModel(get())
    }

    // для Settings
    viewModel {
        SettingsViewModel(get(), get())
    }

    // для Player
    viewModel { (track: Track) ->
        PlayerViewModel(get(), track)
    }

    // для фрагмента Favorites
    viewModel {
        FavoritesViewModel()
    }

    // для фрагмента Playlist
    viewModel {
        PlaylistViewModel()
    }
}