package com.a2t.myapplication.mediateca.domaim.api

import com.a2t.myapplication.mediateca.data.db.entity.TrackEntity
import com.a2t.myapplication.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesTracksRepository {

    fun insertTrack(track: TrackEntity)

    fun deleteTrack (track: TrackEntity)

    fun getTracks(): Flow<List<Track>>
}