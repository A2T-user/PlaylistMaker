package com.a2t.myapplication.search.domain.impl

import com.a2t.myapplication.search.data.dto.api.SearchingHistory
import com.a2t.myapplication.search.domain.api.SearchInteractor
import com.a2t.myapplication.search.domain.api.SearchRepository
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchInteractorImpl(
    private val repository: SearchRepository,
    private val searchHistory: SearchingHistory
    ) : SearchInteractor {

    override fun searchTracks(entity: String, expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { result ->
            when(result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }
                is Resource.Error -> {
                    Pair(null, result.message)
                }
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
        track: Track
    ): ArrayList<Track> {
        return searchHistory.addTrackToSearchHistory(track)
    }

    override fun processingSearchHistory(): Flow<ArrayList<Track>> {
        return repository.processingSearchHistory()
    }

    override fun getFavoritesIdList(): Flow<List<Int>> {
        return repository.getFavoritesIdList()
    }
}