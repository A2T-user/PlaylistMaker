package com.a2t.myapplication.player.ui.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.a2t.myapplication.player.domain.api.PlayerInteractor
import com.a2t.myapplication.search.domain.models.Track

class PlayerViewModel (
    private val playerInteractor: PlayerInteractor,
    track: Track?
): ViewModel() {
    init {
        setDataSource(track?.previewUrl)
        preparePlayer()
        setOnPreparedListener {
            statePlayerLiveData.postValue(PlayerState.STATE_PREPARED)
        }
        setOnCompletionListener {
            statePlayerLiveData.postValue(PlayerState.STATE_PREPARED)
        }
    }

    private var statePlayerLiveData = MutableLiveData(trackAnalysis(track))

    // Анализ полученого трека
    private fun trackAnalysis (track: Track?): PlayerState {
        return if (track == null) {
            PlayerState.STATE_ERROR
        } else {
            if (track.collectionName.isNotEmpty()) PlayerState.STATE_DEFAULT else PlayerState.STATE_NO_ALBUM_NAME
        }
    }
    // Получение состояния плеера
    fun getStatePlayerLiveData(): LiveData<PlayerState> = statePlayerLiveData

    // Изменение состояния плеера
    fun updateStatePlayerLiveData(state: PlayerState) {
        if (statePlayerLiveData.value != state) {
            statePlayerLiveData.postValue(state)
        }
    }

    // Изменение состояния плеера после клика по кнопке Play
    fun changeStatePlayerAfterClick () {
        when (statePlayerLiveData.value) {
            PlayerState.STATE_PLAYING -> statePlayerLiveData.postValue(PlayerState.STATE_PAUSED)
            PlayerState.STATE_PAUSED, PlayerState.STATE_PREPARED -> statePlayerLiveData.postValue(PlayerState.STATE_PLAYING)
            else -> {}
        }
    }

    // Плеер
    private fun setDataSource(url: String?) {
        playerInteractor.setDataSource(url)
    }

    private fun preparePlayer() {
        playerInteractor.preparePlayer()
    }

    fun start() {
        playerInteractor.start()
    }

    fun pause() {
        playerInteractor.pause()
    }

    fun currentPosition(): String {
        return playerInteractor.currentPosition()
    }

    private fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        playerInteractor.setOnPreparedListener(listener)
    }

    private fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        playerInteractor.setOnCompletionListener(listener)
    }

    private fun release () {
        playerInteractor.release()
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }
}