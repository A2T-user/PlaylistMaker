package com.a2t.myapplication.сreateplaylist.domain.api

import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface CreatePlaylistInteractor {

    // Добавление плейлиста
    fun addNewPlaylist(playlist: Playlist): Flow<Long>         // Возвращает id добавленного плейлиста

    fun updatePlaylist(playlist: Playlist): Flow<Int>

    // Копирует обложку плей листа в хранилище приложения
    fun saveImageToPrivateStorage(uri: String): String

    // Добавление трека в таблицу 'tracks_from_playlists_table'
    fun addTrackInPlaylist(track: Track)

}