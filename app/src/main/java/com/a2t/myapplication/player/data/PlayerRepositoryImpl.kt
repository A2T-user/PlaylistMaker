package com.a2t.myapplication.player.data

import android.media.MediaPlayer
import com.a2t.myapplication.player.domain.api.PlayerRepository
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl: PlayerRepository {
    val player = MediaPlayer()

    override fun setDataSource(url: String?) {
        player.setDataSource(url)
    }

    override fun preparePlayer() {
        player.prepareAsync()
    }

    override fun start() {
        player.start()
    }

    override fun pause() {
        player.pause()
    }

    override fun currentPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(player.currentPosition)
    }

    override fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        player.setOnPreparedListener(listener)
    }

    override fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        player.setOnCompletionListener (listener)
    }

    override fun release() {
        player.release()
    }
}