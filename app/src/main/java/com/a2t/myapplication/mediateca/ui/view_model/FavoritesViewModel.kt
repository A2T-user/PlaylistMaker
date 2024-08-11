package com.a2t.myapplication.mediateca.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a2t.myapplication.mediateca.domaim.db.FavoritesTracksInteractor
import com.a2t.myapplication.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FavoritesViewModel(
    private val favoritesTracksInteractor: FavoritesTracksInteractor
) : ViewModel() {

    private var favoritesLiveData = MutableLiveData(listOf<Track>())

    fun getFavorites () {
        viewModelScope.launch(Dispatchers.IO) {
            favoritesTracksInteractor
                .getTracks()
                .collect {favoritesLiveData.postValue(updatFavorites(it))}
        }
    }

    fun getFavoritesLiveData(): LiveData<List<Track>> = favoritesLiveData

    // Проходит по массиву треков, проверяя есть ли они в избранном
    private fun updatFavorites (tracks: List<Track>): List<Track> {
        tracks.forEach { track -> track.isFavorite = true }
        return tracks
    }

}

