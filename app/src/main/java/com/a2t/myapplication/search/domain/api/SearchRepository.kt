package com.a2t.myapplication.search.domain.api

import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.util.Resource
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
}