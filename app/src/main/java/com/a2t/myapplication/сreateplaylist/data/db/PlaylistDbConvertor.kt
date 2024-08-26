package com.a2t.myapplication.сreateplaylist.data.db

import com.a2t.myapplication.сreateplaylist.data.db.entity.PlaylistEntity
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import com.google.gson.Gson

class PlaylistDbConvertor (
    private val gson: Gson
) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistUri,
            playlist.playlistDescription,
            gson.toJson(playlist.playlistIdList)
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.id,
            playlist.playlistName,
            playlist.playlistUri,
            playlist.playlistDescription,
            gson.fromJson(playlist.playlistIdList, Array<Int>::class.java).toCollection(ArrayList())
        )
    }
}