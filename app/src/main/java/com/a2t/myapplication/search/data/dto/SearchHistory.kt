package com.a2t.myapplication.search.data.dto

import android.content.Context
import com.a2t.myapplication.appContext
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.search.data.dto.api.SearchingHistory
import com.google.gson.Gson

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"     // Имя файла для сохраняемых параметров
const val SEARCH_HISTORY_KEY = "search_history"                             // Ключ для истории поиска
const val MAX_COUNT_TRACKS_IN_SEARCH_HISTORY = 10 // Максимальное число треков в истории поиска
class SearchHistory: SearchingHistory {

    private val sharedPrefs = appContext.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)

    // Чтение истории поиска из SharedPreferences и возврат в ArrayList<Track>
    override fun readSearchHistory (): ArrayList<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY_KEY, null) ?: return arrayListOf()
        return Gson().fromJson(json, Array<Track>::class.java).toCollection(ArrayList())
    }

    // Переводит Array<Track> в строку JSON и записывает в SharedPreferences
    private fun writeSearchHistory(searchHistory: ArrayList<Track>) {
        val json = Gson().toJson(searchHistory)
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }

    // Очистка истории поиска и запись результатов в SharedPreferences
    override fun clearSearchHistory () {
        writeSearchHistory(arrayListOf())
    }

    // Добавление Trac в ArrayList<Track> и запись результатов в SharedPreferences, возвращает преобразованный список
    override fun addTrackToSearchHistory (track: Track) : ArrayList<Track> {
        val searchHistoryList = readSearchHistory()
        // Есть ли track уже в избранном удаляем его старую запись
        searchHistoryList.remove(track)
        // Добавление track в начало searchHistory
        searchHistoryList.add(0, track)
        // Удаляем 11-й элемент
        while (searchHistoryList.size > MAX_COUNT_TRACKS_IN_SEARCH_HISTORY) searchHistoryList
            .removeAt(MAX_COUNT_TRACKS_IN_SEARCH_HISTORY)
        // Записываем получивщийся список в SharedPreferences
        writeSearchHistory(searchHistoryList)

        return searchHistoryList
    }
}