package com.a2t.myapplication.search.data

import com.a2t.myapplication.search.data.dto.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response

}