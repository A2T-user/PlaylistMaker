package com.a2t.myapplication.search.domain.api

import com.a2t.myapplication.search.domain.models.Track

interface SearchHistoryRepository {
    fun readSearchHistory (): ArrayList<Track>
    fun clearSearchHistory ()
    fun addTrackToSearchHistory (track: Track) : ArrayList<Track>
}