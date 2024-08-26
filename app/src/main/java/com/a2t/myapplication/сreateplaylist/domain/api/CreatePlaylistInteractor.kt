package com.a2t.myapplication.сreateplaylist.domain.api

import android.net.Uri
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface CreatePlaylistInteractor {

    // Добавление плейлиста
    fun addNewPlaylist(playlist: Playlist): Flow<Long>         // Возвращает id добавленного плейлиста

    fun updatePlaylist(playlist: Playlist): Flow<Int>

    // Копирует обложку плей листа в хранилище приложения
    fun saveImageToPrivateStorage(uri: Uri): Uri

}