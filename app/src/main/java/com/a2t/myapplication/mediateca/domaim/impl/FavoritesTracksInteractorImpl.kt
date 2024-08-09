package com.a2t.myapplication.mediateca.domaim.impl

import com.a2t.myapplication.mediateca.domaim.api.FavoritesTracksRepository
import com.a2t.myapplication.mediateca.domaim.db.FavoritesTracksInteractor
import com.a2t.myapplication.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesTracksInteractorImpl(
    private val repository: FavoritesTracksRepository
): FavoritesTracksInteractor {

    override fun getTracks(): Flow<List<Track>> {
        return repository.getTracks().map { it.reversed() }
    }
}