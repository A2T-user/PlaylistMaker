package com.a2t.myapplication.mediateca.domain.db

import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun getPlaylists(): Flow<List<Playlist>>

    fun deleteTrackById(trackId: Int)

}