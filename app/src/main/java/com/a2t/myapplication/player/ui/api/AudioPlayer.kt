package com.a2t.myapplication.player.ui.api

import android.media.MediaPlayer

interface AudioPlayer {
    fun setDataSource(url: String?)
    fun preparePlayer()
    fun start()
    fun pause()
    fun currentPosition(): String
    fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener)
    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener)
    fun release()
}