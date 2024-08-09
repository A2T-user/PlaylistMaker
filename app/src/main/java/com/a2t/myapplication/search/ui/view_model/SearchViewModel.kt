package com.a2t.myapplication.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a2t.myapplication.search.domain.api.SearchInteractor
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.search.ui.models.FilterScreenMode
import com.a2t.myapplication.search.ui.models.SearchData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor
): ViewModel() {

    private var searchLiveData = MutableLiveData(SearchData(FilterScreenMode.HISTORY, listOf(), null))

    fun processingRequest (requestText: String) {
        if (requestText.isNotEmpty()) {
            searchLiveData.postValue(SearchData(FilterScreenMode.SEARCH, null, null))
            viewModelScope.launch(Dispatchers.IO) {
                searchInteractor
                    .searchTracks("song", requestText)
                    .collect { pair ->
                    processResult(pair.first, pair.second)
                }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        if (foundTracks != null) {
            if (foundTracks.isNotEmpty()) {
                searchLiveData.postValue(SearchData(FilterScreenMode.SEARCHING_RESULTS, foundTracks, null))
            } else {
                searchLiveData.postValue(SearchData(FilterScreenMode.NOTHING, foundTracks, null))
            }
        } else if (errorMessage != null) {
            searchLiveData.postValue((SearchData(FilterScreenMode.ERROR, null, errorMessage)))
        } else {
            searchLiveData.postValue((SearchData(FilterScreenMode.ERROR, null, "Что-то пошло не так")))
        }
    }

    fun processingSearchHistory () {
        viewModelScope.launch(Dispatchers.IO) {
            searchInteractor
                .processingSearchHistory()
                .collect {searchLiveData.postValue(SearchData(FilterScreenMode.HISTORY, it, null))}
        }
    }

    // Обновляет список рециклера на предмет возможного изменения Избранного
    fun updateSearchingResultsFavorite (tracks: ArrayList<Track>) {
        viewModelScope.launch(Dispatchers.IO) {
            searchInteractor
                .getFavoritesIdList()
                .collect {updatFavorites(tracks, it)}
        }
    }

    // Проходит по массиву треков, проверяя есть ли они в избранном
    private fun updatFavorites (tracks: ArrayList<Track>, favorites: List<Int>): ArrayList<Track> {
        tracks.forEach { track -> track.isFavorite = favorites.find { it == track.trackId } != null }
        return tracks
    }

    fun getSearchLiveData(): LiveData<SearchData> = searchLiveData

    fun clearSearchHistory() {
        searchInteractor.clearSearchHistory()
    }

    fun addTrackToSearchHistory(
        track: Track
    ): ArrayList<Track> {
        return searchInteractor.addTrackToSearchHistory(track)
    }
}