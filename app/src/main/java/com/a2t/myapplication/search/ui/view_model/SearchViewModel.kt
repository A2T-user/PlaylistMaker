package com.a2t.myapplication.search.ui.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.a2t.myapplication.creator.SearchCreator
import com.a2t.myapplication.search.domain.api.SearchInteractor
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.search.ui.models.FilterScreenMode
import com.a2t.myapplication.search.ui.models.SearchData

class SearchViewModel(
    private val searchInteractor: SearchInteractor
): ViewModel() {

    private var searchLiveData = MutableLiveData(SearchData(FilterScreenMode.HISTORY, listOf(), null))

    fun processingRequest (requestText: String) {
        if (requestText.isNotEmpty()) {
            searchLiveData.postValue(SearchData(FilterScreenMode.SEARCH, null, null))
            searchInteractor.searchTracks("song", requestText,
                object : SearchInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
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
                }
            )
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