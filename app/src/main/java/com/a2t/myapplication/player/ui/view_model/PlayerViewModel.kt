package com.a2t.myapplication.player.ui.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a2t.myapplication.player.domain.api.PlayerInteractor
import com.a2t.myapplication.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val REFRESH_PROGRESS_DELAY = 300L

class PlayerViewModel (
    private val playerInteractor: PlayerInteractor,
    track: Track?
): ViewModel() {

    private var timerJob: Job? = null

    private var statePlayerLiveData = MutableLiveData<PlayerState>(PlayerState.Default())

    // Получение состояния плеера
    fun getStatePlayerLiveData(): LiveData<PlayerState> = statePlayerLiveData

    init {
        setDataSource(track?.previewUrl)
        preparePlayer()
        setOnPreparedListener {
            statePlayerLiveData.postValue(PlayerState.Prepared())
        }
        setOnCompletionListener {
            statePlayerLiveData.postValue(PlayerState.Prepared())
        }
    }

    // Изменение состояния плеера после клика по кнопке Play
    fun changeStatePlayerAfterClick () {
        when (statePlayerLiveData.value) {
            is PlayerState.Playing -> pause()
            is PlayerState.Paused, is PlayerState.Prepared -> start()
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

    private fun start() {
        playerInteractor.start()
        statePlayerLiveData.postValue(PlayerState.Playing(currentPosition()))
        startTimer()
    }

    fun pause() {
        playerInteractor.pause()
        timerJob?.cancel()
        statePlayerLiveData.postValue(PlayerState.Paused(currentPosition()))

    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (isPlaying()) {
                delay(REFRESH_PROGRESS_DELAY)
                if (statePlayerLiveData.value is PlayerState.Playing) {
                    statePlayerLiveData.postValue(PlayerState.Playing(currentPosition()))
                }
            }
        }
    }

    private fun currentPosition(): String {
        return playerInteractor.currentPosition()
    }

    private fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        playerInteractor.setOnPreparedListener(listener)
    }

    private fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        playerInteractor.setOnCompletionListener(listener)
    }

    private fun isPlaying (): Boolean {
        return playerInteractor.isPlaying()
    }


    private fun release () {
        playerInteractor.release()
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }






}