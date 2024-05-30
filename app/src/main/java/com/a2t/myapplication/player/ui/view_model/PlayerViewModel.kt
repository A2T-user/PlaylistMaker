package com.a2t.myapplication.player.ui.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.a2t.myapplication.creator.PlayerCreator
import com.a2t.myapplication.player.domain.api.PlayerInteractor
import com.a2t.myapplication.search.domain.models.Track

class PlayerViewModel (
    private val playerInteractor: PlayerInteractor,
    track: Track?
): ViewModel() {

    private var statePlayerLiveData = MutableLiveData(trackAnalysis(track))

    // Анализ полученого трека
    private fun trackAnalysis (track: Track?): PlayerState {
        if (track == null) {
            return PlayerState.STATE_ERROR
        } else {
            return if (track.collectionName.isNotEmpty()) PlayerState.STATE_DEFAULT else PlayerState.STATE_NO_ALBUM_NAME
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
    fun setDataSource(url: String?) {
        playerInteractor.setDataSource(url)
    }

    fun preparePlayer() {
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

    fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        playerInteractor.setOnPreparedListener(listener)
    }

    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        playerInteractor.setOnCompletionListener(listener)
    }

    fun release() {
        playerInteractor.release()
    }

    companion object {
        fun getViewModelFactory(track: Track?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(
                    PlayerCreator.providePlayerInteractor(),
                    track

                )
            }
        }
    }
}