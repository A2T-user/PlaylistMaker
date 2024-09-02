package com.a2t.myapplication.сreateplaylist.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "playlist_table")
class PlaylistEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,                       // Первичный ключ
    val playlistName: String,           // Название плейлиста
    val playlistUri: String?,           // URI обложки плейлиста
    val playlistDescription: String?,   // Описание плейлиста
    val playlistIdList: String,          // Список идентификаторов треков плейлиста в виде строки
)
