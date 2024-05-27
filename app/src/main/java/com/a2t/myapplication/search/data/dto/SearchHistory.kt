package com.a2t.myapplication.search.data.dto

import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.main.ui.SEARCH_HISTORY_KEY
import com.a2t.myapplication.main.ui.sharedPrefs
import com.a2t.myapplication.search.data.dto.api.SearchingHistory
import com.google.gson.Gson


const val MAX_COUNT_TRACKS_IN_SEARCH_HISTORY = 10 // Максимальное число треков в истории поиска
class SearchHistory: SearchingHistory {

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
    override fun addTrackToSearchHistory (searchHistoryList: ArrayList<Track>, track: Track) : ArrayList<Track> {
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