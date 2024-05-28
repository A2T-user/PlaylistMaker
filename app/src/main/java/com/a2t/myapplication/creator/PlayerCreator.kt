package com.a2t.myapplication.creator

import com.a2t.myapplication.player.data.PlayerRepositoryImpl
import com.a2t.myapplication.player.domain.api.PlayerRepository
import com.a2t.myapplication.player.domain.api.PlayerInteractor
import com.a2t.myapplication.player.domain.impl.PlayerInteractorImpl

object PlayerCreator {
    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }
}