package com.a2t.myapplication.mediateca.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_table")
class TrackEntity (
    @PrimaryKey
    val trackId: Int,                   // ID трека
    val trackName: String,              // Название композиции
    val artistName: String,             // Имя исполнителя
    val collectionName: String,         // Название альбома
    val releaseDate: String?,           // Год релиза трека -> releaseDate=2015-07-27T12:00:00Z
    val primaryGenreName: String,       // Жанр трека
    val country: String,                // Страна исполнителя
    val trackTime: String,              // Продолжительность трека в формате "mm:ss"
    val artworkUrl100: String,          // Ссылка на изображение обложки Малый
    val artworkUrl512: String,          // Ссылка на изображение обложки Большой
    val previewUrl: String?             // URL отрывка трека
)
