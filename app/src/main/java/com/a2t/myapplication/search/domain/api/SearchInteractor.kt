package com.a2t.myapplication.search.domain.api

import com.a2t.myapplication.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    // Поиск
    fun searchTracks(entity: String, expression: String): Flow<Pair<List<Track>?, String?>>

    // История поиска
    fun readSearchHistory (): ArrayList<Track>
    fun clearSearchHistory ()
    fun addTrackToSearchHistory (track: Track) : ArrayList<Track>
    fun processingSearchHistory(): Flow<ArrayList<Track>>
    fun getFavoritesIdList(): Flow<List<Int>>
}