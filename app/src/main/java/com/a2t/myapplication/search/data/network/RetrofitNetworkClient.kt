package com.a2t.myapplication.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.a2t.myapplication.search.data.NetworkClient
import com.a2t.myapplication.search.data.dto.Response
import com.a2t.myapplication.search.data.dto.SearchRequest
import java.net.SocketTimeoutException

class RetrofitNetworkClient(
    private val iTunesService: ItunesApi,
    private val context: Context
) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = -1 }
        }
        if (dto !is SearchRequest) {
            return Response().apply { resultCode = 400 }
        }
        try {
            val resp = iTunesService.search("song", dto.expression).execute()

            val body = resp.body() ?: Response()

            return if (body != null) {
                body.apply { resultCode = resp.code() }
            } else {
                Response().apply { resultCode = resp.code() }
            }
        } catch (e: SocketTimeoutException) {
            return Response().apply { resultCode = -1 }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}