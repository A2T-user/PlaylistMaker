package com.a2t.myapplication.сreateplaylist.domain.db

import com.a2t.myapplication.player.data.db.entity.TrackFromPlaylistsEntity
import com.a2t.myapplication.сreateplaylist.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

interface CreatePlaylistRepository {

    // Добавление плейлиста
    fun addNewPlaylist(playlist: PlaylistEntity): Flow<Long>         // Возвращает id добавленного плейлиста

    // Обновление плейлиста
    fun updatePlaylist(playlist: PlaylistEntity): Flow<Int>

    // Копирует обложку плей листа в хранилище приложения
    fun saveImageToPrivateStorage(uri: String): String

    // Добавление трека в таблицу 'tracks_from_playlists_table'
    fun addTrackInPlaylist(track: TrackFromPlaylistsEntity)

}