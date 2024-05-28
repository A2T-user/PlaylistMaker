package com.a2t.myapplication.search.domain.api

import com.a2t.myapplication.search.domain.models.Track

interface SearchInteractor {
    // Поиск
    fun searchTracks(entity: String, expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
    // История поиска
    fun readSearchHistory (): ArrayList<Track>
    fun clearSearchHistory ()
    fun addTrackToSearchHistory (track: Track) : ArrayList<Track>
}