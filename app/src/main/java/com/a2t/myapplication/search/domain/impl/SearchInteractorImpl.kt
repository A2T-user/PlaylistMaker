package com.a2t.myapplication.search.domain.impl

import com.a2t.myapplication.search.data.dto.SearchHistory
import com.a2t.myapplication.search.domain.api.SearchInteractor
import com.a2t.myapplication.search.domain.api.SearchRepository
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.util.Resource
import java.util.concurrent.Executors

class SearchInteractorImpl(
    private val repository: SearchRepository,
    private val searchHistory: SearchHistory
    ) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(entity: String, expression: String, consumer: SearchInteractor.TracksConsumer) {
        executor.execute {
            when(val resource = repository.searchTracks(expression)) {
                is Resource.Success -> { consumer.consume(resource.data, null) }
                is Resource.Error -> { consumer.consume(null, resource.message) }
            }
        }
    }

    override fun readSearchHistory(): ArrayList<Track> {
        return searchHistory.readSearchHistory()
    }

    override fun clearSearchHistory() {
        searchHistory.clearSearchHistory()
    }

    override fun addTrackToSearchHistory(
        searchHistoryList: ArrayList<Track>,
        track: Track
    ): ArrayList<Track> {
        return searchHistory.addTrackToSearchHistory(searchHistoryList, track)
    }
}