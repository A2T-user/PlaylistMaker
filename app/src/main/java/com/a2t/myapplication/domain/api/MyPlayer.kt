package com.a2t.myapplication.domain.api

import android.media.MediaPlayer

interface MyPlayer {
    fun setDataSource(url: String?)
    fun preparePlayer()
    fun start()
    fun pause()
    fun currentPosition(): String
    fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener)
    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener)
    fun release()
}