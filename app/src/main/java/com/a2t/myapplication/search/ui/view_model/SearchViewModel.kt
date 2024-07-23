package com.a2t.myapplication.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a2t.myapplication.search.domain.api.SearchInteractor
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.search.ui.models.FilterScreenMode
import com.a2t.myapplication.search.ui.models.SearchData
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor
): ViewModel() {

    private var searchLiveData = MutableLiveData(SearchData(FilterScreenMode.HISTORY, listOf(), null))

    fun processingRequest (requestText: String) {
        if (requestText.isNotEmpty()) {
            searchLiveData.postValue(SearchData(FilterScreenMode.SEARCH, null, null))
            viewModelScope.launch {
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
        searchLiveData.postValue(SearchData(
            FilterScreenMode.HISTORY,
            searchInteractor.readSearchHistory(),
            null
            )
        )
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