package com.a2t.myapplication.domain.impl

import android.media.MediaPlayer
import com.a2t.myapplication.domain.api.AudioPlayer
import com.a2t.myapplication.domain.api.PlayerInteractor

class PlayerInteractorImpl (myPlayer: AudioPlayer): PlayerInteractor {
    private val player = myPlayer
    override fun setDataSource(url: String?) {
        player.setDataSource(url)
    }

    override fun preparePlayer() {
        player.preparePlayer()
    }

    override fun start() {
        player.start()
    }

    override fun pause() {
        player.pause()
    }

    override fun currentPosition(): String {
        return player.currentPosition()
    }

    override fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        player.setOnPreparedListener(listener)
    }

    override fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        player.setOnCompletionListener(listener)
    }

    override fun release() {
        player.release()
    }


}