package com.a2t.myapplication.search.ui.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.a2t.myapplication.creator.SearchCreator
import com.a2t.myapplication.search.domain.api.SearchInteractor
import com.a2t.myapplication.search.domain.models.Track

class SearchViewModel(
    private val searchInteractor: SearchInteractor
): ViewModel() {

    fun searchTracks(entity: String, expression: String, consumer: SearchInteractor.TracksConsumer) {
        searchInteractor.searchTracks(entity, expression, consumer)
    }

    // История поиска
    fun readSearchHistory(): ArrayList<Track> {
        return searchInteractor.readSearchHistory()
    }

    fun clearSearchHistory() {
        searchInteractor.clearSearchHistory()
    }

    fun addTrackToSearchHistory(
        searchHistoryList: ArrayList<Track>,
        track: Track
    ): ArrayList<Track> {
        return searchInteractor.addTrackToSearchHistory(searchHistoryList, track)
    }






    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(
                    SearchCreator.provideSearchInteractor(context)
                )
            }
        }
    }
}