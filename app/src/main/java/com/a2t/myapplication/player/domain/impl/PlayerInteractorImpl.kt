package com.a2t.myapplication.player.domain.impl

import android.media.MediaPlayer
import com.a2t.myapplication.player.domain.api.PlayerRepository
import com.a2t.myapplication.player.domain.api.PlayerInteractor

class PlayerInteractorImpl (
    private val repository: PlayerRepository
): PlayerInteractor {
    override fun setDataSource(url: String?) {
        repository.setDataSource(url)
    }

    override fun preparePlayer() {
        repository.preparePlayer()
    }

    override fun start() {
        repository.start()
    }

    override fun pause() {
        repository.pause()
    }

    override fun currentPosition(): String {
        return repository.currentPosition()
    }

    override fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        repository.setOnPreparedListener(listener)
    }

    override fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        repository.setOnCompletionListener(listener)
    }

    override fun release() {
        repository.release()
    }
}