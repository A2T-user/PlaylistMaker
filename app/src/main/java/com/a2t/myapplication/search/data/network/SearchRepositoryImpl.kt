package com.a2t.myapplication.search.data.network

import com.a2t.myapplication.search.data.NetworkClient
import com.a2t.myapplication.search.data.dto.SearchRequest
import com.a2t.myapplication.search.data.dto.SearchResponse
import com.a2t.myapplication.search.domain.api.SearchRepository
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.util.Resource

class SearchRepositoryImpl(
    private val networkClient: NetworkClient
) : SearchRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(SearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }
            200 -> {
                Resource.Success((response as SearchResponse).results.map {
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
                })
            }
            else -> {
                    Resource.Error("Ошибка сервера")
            }
        }
    }
}