package com.a2t.myapplication.mediateca.domain.api

import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun getPlaylist(): Flow<List<Playlist>>

    fun getPlaylistById(playlistId: Long): Flow<Playlist>

    fun getPlaylistTrackList(playlistIdList: List<Int>): Flow<List<Track>>

    fun deleteTrackById (trackId:Int)

    fun deletePlaylistById (playlistId:Long)
}