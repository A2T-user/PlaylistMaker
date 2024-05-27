package com.a2t.myapplication.search.data.dto.api

import com.a2t.myapplication.search.domain.models.Track

interface SearchingHistory {
    fun readSearchHistory (): ArrayList<Track>
    fun clearSearchHistory ()
    fun addTrackToSearchHistory (searchHistoryList: ArrayList<Track>, track: Track) : ArrayList<Track>
}