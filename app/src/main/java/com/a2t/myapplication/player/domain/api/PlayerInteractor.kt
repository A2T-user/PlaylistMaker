package com.a2t.myapplication.player.domain.api

import android.media.MediaPlayer

interface PlayerInteractor {
    fun setDataSource(url: String?)
    fun preparePlayer()
    fun start()
    fun pause()
    fun currentPosition(): String
    fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener)
    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener)
    fun release()
}