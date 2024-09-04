package com.a2t.myapplication.showplaylist.domain.db

import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface ShowPlaylistInteractor {

    fun getPlaylistById (playlistId: Long): Flow<Playlist>

    fun getPlaylistTrackList(playlistIdList: List<Int>): Flow<List<Track>>

    fun deletePlaylistById (playlistId:Long)

}