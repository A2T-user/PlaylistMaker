package com.a2t.myapplication.search.data.network

import com.a2t.myapplication.mediateca.data.db.AppDatabase
import com.a2t.myapplication.search.data.NetworkClient
import com.a2t.myapplication.search.data.dto.SearchRequest
import com.a2t.myapplication.search.data.dto.SearchResponse
import com.a2t.myapplication.search.data.dto.api.SearchingHistory
import com.a2t.myapplication.search.domain.api.SearchRepository
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val searchHistory: SearchingHistory,
    private val appDatabase: AppDatabase,
    private val networkClient: NetworkClient
) : SearchRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {

        val response = networkClient.doRequest(SearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }
            200 -> {
                val favoritesIdList = appDatabase.getTrackDao().getTracksId()
                emit(Resource.Success((response as SearchResponse).results.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.trackDurationInString(),
                    it.artworkUrl100,
                    it.getArtworkUrl512(),
                    it.previewUrl,
                    isFavorite(it.trackId, favoritesIdList)
                )
                }))
            }
            else -> {
                    emit(Resource.Error("Ошибка сервера"))
            }
        }
    }

    override fun processingSearchHistory(): Flow<ArrayList<Track>> = flow{
        val searchHistoryList = searchHistory.readSearchHistory()
        val favoritesIdList = appDatabase.getTrackDao().getTracksId()
        if (favoritesIdList.isNotEmpty()) searchHistoryList.forEach { it.isFavorite = isFavorite(it.trackId, favoritesIdList) }
        emit(searchHistoryList)

    }

    override fun getFavoritesIdList(): Flow<List<Int>> = flow{
        emit(appDatabase.getTrackDao().getTracksId())
    }


    private fun isFavorite (trackId: Int, favoritesIdList: List<Int>): Boolean {
        val favorite = favoritesIdList.find { it == trackId }
        return favorite != null
    }
}
