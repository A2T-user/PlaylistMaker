package com.a2t.myapplication.creator

import com.a2t.myapplication.player.data.AudioPlayerImpl
import com.a2t.myapplication.player.domain.api.AudioPlayer
import com.a2t.myapplication.player.domain.api.PlayerInteractor
import com.a2t.myapplication.player.domain.impl.PlayerInteractorImpl

object PlayerCreator {
    private fun getAudioPlayer(): AudioPlayer {
        return AudioPlayerImpl()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getAudioPlayer())
    }
}