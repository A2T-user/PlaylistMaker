package com.a2t.myapplication.mediateca.domain.db

import com.a2t.myapplication.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesTracksInteractor {

    fun getTracks(): Flow<List<Track>>
}