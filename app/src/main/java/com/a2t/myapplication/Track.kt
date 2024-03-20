package com.a2t.myapplication

data class Track(
    val trackName: String,              // Название композиции
    val artistName: String,             // Имя исполнителя
    val trackTimeMillis: Long,        // Продолжительность трека, милисекунды
    val artworkUrl100: String           // Ссылка на изображение обложки
)
