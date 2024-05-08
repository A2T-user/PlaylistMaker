package com.a2t.myapplication.domain.api

import com.a2t.myapplication.domain.models.Track
import com.a2t.myapplication.presentation.MyPlayerImpl

class PlayerInteractor {
    // Получить объект Track
    fun getTrack (trackProvider:TrackProvider): Track? {
        return trackProvider.getTrack()
    }

    // Подготовка экрана
    fun screenPreparation (track: Track?, executor: ScreenExecutor) {
        executor.execute(track)
    }

    // Создаем плеер
    fun createPlayer (): MyPlayer {
        return MyPlayerImpl()
    }


}