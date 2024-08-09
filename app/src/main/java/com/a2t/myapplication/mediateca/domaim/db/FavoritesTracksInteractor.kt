package com.a2t.myapplication.mediateca.domaim.db

import com.a2t.myapplication.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesTracksInteractor {

    fun getTracks(): Flow<List<Track>>
}