package com.a2t.myapplication

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi{
    @GET("/search")
    fun search(@Query("entity") entity: String, @Query("term") text: String) : Call<TracksResponse>
}