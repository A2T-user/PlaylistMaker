package com.a2t.myapplication.search.data.network

import com.a2t.myapplication.search.data.dto.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi{
    @GET("/search")
    suspend fun search(@Query("entity") entity: String, @Query("term") text: String) : SearchResponse
}