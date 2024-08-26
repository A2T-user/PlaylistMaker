package com.a2t.myapplication.сreateplaylist.domain.model

data class Playlist(
    val playlistId: Long,               // id плейлиста
    val playlistName: String,           // Название плейлиста
    val playlistUri: String?,           // URI обложки плейлиста
    val playlistDescription: String?,   // Описание плейлиста
    val playlistIdList: MutableList<Int>,      // Список идентификаторов треков плейлиста
)
