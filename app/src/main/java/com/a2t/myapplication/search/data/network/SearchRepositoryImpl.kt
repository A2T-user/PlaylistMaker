package com.a2t.myapplication.search.data.network

import com.a2t.myapplication.search.data.NetworkClient
import com.a2t.myapplication.search.data.dto.SearchRequest
import com.a2t.myapplication.search.data.dto.SearchResponse
import com.a2t.myapplication.search.domain.api.SearchRepository
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val networkClient: NetworkClient
) : SearchRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(SearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }
            200 -> {
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
                    it.previewUrl
                )
                }))
            }
            else -> {
                    emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
}