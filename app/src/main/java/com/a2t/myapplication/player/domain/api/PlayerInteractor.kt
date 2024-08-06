package com.a2t.myapplication.player.domain.api

import android.media.MediaPlayer
import com.a2t.myapplication.search.domain.models.Track

interface PlayerInteractor {
    fun setDataSource(url: String?)
    fun preparePlayer()
    fun start()
    fun pause()
    fun currentPosition(): String
    fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener)
    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener)
    fun isPlaying (): Boolean
    fun release()
    fun onFavoriteClicked(track: Track)
}