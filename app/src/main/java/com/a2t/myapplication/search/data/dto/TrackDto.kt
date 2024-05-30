package com.a2t.myapplication.search.data.dto

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

data class TrackDto (
    val trackId: Int,                   // ID трека
    val trackName: String,              // Название композиции
    val artistName: String,             // Имя исполнителя
    val collectionName: String,         // Название альбома
    val releaseDate: String,            // Год релиза трека -> releaseDate=2015-07-27T12:00:00Z
    val primaryGenreName: String,       // Жанр трека
    val country: String,                // Страна исполнителя
    val trackTimeMillis: Long,          // Продолжительность трека, милисекунды
    val artworkUrl100: String,          // Ссылка на изображение обложки
    val previewUrl: String              // URL отрывка трека
): Serializable
{
    fun trackDurationInString (): String = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
    fun getArtworkUrl512(): String = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
}