package com.a2t.myapplication.search.domain.api

import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.util.Resource

interface SearchRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}