package com.a2t.myapplication.player.domain.api

import android.media.MediaPlayer

interface PlayerRepository {
    fun setDataSource(url: String?)
    fun preparePlayer()
    fun start()
    fun pause()
    fun currentPosition(): String
    fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener)
    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener)
    fun isPlaying (): Boolean
    fun release()
}