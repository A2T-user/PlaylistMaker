package com.a2t.myapplication.mediateca.domain.impl

import com.a2t.myapplication.mediateca.domain.api.FavoritesTracksRepository
import com.a2t.myapplication.mediateca.domain.db.FavoritesTracksInteractor
import com.a2t.myapplication.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesTracksInteractorImpl(
    private val repository: FavoritesTracksRepository
): FavoritesTracksInteractor {

    override fun getTracks(): Flow<List<Track>> {
        return repository.getTracks()
    }
}