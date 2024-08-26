package com.a2t.myapplication.mediateca.domaim.api

import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun getPlaylist(): Flow<List<Playlist>>

}