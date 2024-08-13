package com.a2t.myapplication.player.domain.api

import com.a2t.myapplication.search.domain.models.Track

interface PlayerInteractor {
    fun onFavoriteClicked(track: Track)
}