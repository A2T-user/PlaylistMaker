package com.a2t.myapplication.player.ui.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.a2t.myapplication.player.ui.api.AudioPlayer
import com.a2t.myapplication.player.ui.AudioPlayerImpl

class PlayerViewModel (
    private val player: AudioPlayer
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
    fun setDataSource(url: String?) {
        player.setDataSource(url)
    }

    fun preparePlayer() {
        player.preparePlayer()
    }

    fun start() {
        player.start()
    }

    fun pause() {
        player.pause()
    }

    fun currentPosition(): String {
        return player.currentPosition()
    }

    fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        player.setOnPreparedListener(listener)
    }

    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        player.setOnCompletionListener(listener)
    }

    fun release() {
        player.release()
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer { PlayerViewModel(AudioPlayerImpl()) }
        }
    }
}