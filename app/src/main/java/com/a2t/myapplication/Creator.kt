package com.a2t.myapplication

import com.a2t.myapplication.data.AudioPlayerImpl
import com.a2t.myapplication.domain.api.AudioPlayer
import com.a2t.myapplication.domain.api.PlayerInteractor
import com.a2t.myapplication.domain.impl.PlayerInteractorImpl

object Creator {
    private fun getAudioPlayer(): AudioPlayer {
        return AudioPlayerImpl()
    }

    fun provideMoviesInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getAudioPlayer())
    }
}