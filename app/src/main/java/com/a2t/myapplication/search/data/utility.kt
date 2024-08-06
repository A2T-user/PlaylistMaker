package com.a2t.myapplication.search.data

fun isFavorite (trackId: Int, favoritesIdList: List<Int>): Boolean {
    val favorite: Int? = favoritesIdList.find { it == trackId }
    return favorite != null
}