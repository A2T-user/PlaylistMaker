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

class PlayerViewModel (
    private val playerInteractor: PlayerInteractor
): ViewModel() {

    private var statePlayerLiveData = MutableLiveData(PlayerData.STATE_DEFAULT)

    // Получение состояния плеера
    fun getStatePlayerLiveData(): LiveData<PlayerData> = statePlayerLiveData

    // Изменение состояния плеера
    fun updateStatePlayerLiveData(state: PlayerData) {
        if (statePlayerLiveData.value != state) {
            statePlayerLiveData.postValue(state)
        }
    }

    // Изменение состояния плеера после клика по кнопке Play
    fun changeStatePlayerAfterClick () {
        when (statePlayerLiveData.value) {
            PlayerData.STATE_PLAYING -> statePlayerLiveData.postValue(PlayerData.STATE_PAUSED)
            PlayerData.STATE_PAUSED, PlayerData.STATE_PREPARED -> statePlayerLiveData.postValue(PlayerData.STATE_PLAYING)
            else -> {}
        }
    }

    // Плеер
    fun getCurrentPosition(): String = playerInteractor.currentPosition()
    fun setDataSource(url: String?) = playerInteractor.setDataSource(url)
    fun preparePlayer() {
        playerInteractor.preparePlayer()
    }
    fun start() {
        playerInteractor.start()
    }
    fun pause() {
        playerInteractor.pause()
    }
    fun release() {
        playerInteractor.release()
    }

    fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        playerInteractor.setOnPreparedListener(listener)
    }

    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        playerInteractor.setOnCompletionListener(listener)
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(
                    PlayerCreator.providePlayerInteractor()
                )
            }
        }
    }
}