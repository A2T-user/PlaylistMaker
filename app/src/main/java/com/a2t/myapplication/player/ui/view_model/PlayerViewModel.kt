package com.a2t.myapplication.player.ui.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a2t.myapplication.player.domain.api.PlayerInteractor
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.сreateplaylist.domain.api.CreatePlaylistInteractor
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import com.a2t.myapplication.сreateplaylist.ui.fragment.CreatePlaylistFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

private const val REFRESH_PROGRESS_DELAY = 300L

class PlayerViewModel (
    private val playerInteractor: PlayerInteractor,
    private val createPlaylistInteractor: CreatePlaylistInteractor,
    track: Track?
): ViewModel() {

    val player = MediaPlayer()
    private var timerJob: Job? = null

    private var statePlayerLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    private val stateFavoritesButtonLiveData = MutableLiveData(track?.isFavorite ?: false)
    private var updatePlaylistsLiveData = MutableLiveData("")

    fun updatePlaylist(playlist: Playlist, track: Track) {
        createPlaylistInteractor.addTrackInPlaylist(track)
        viewModelScope.launch(Dispatchers.IO) {
            createPlaylistInteractor
                .updatePlaylist(playlist)
                .collect {
                    when {
                        it == 1 -> updatePlaylistsLiveData.postValue(playlist.playlistName)
                        else -> updatePlaylistsLiveData.postValue("")
                    }
                }
        }
        CreatePlaylistFragment.isCreatePlaylistFragmentFilled = false
    }

    fun getUpdatePlaylistsLiveData(): LiveData<String> = updatePlaylistsLiveData

    // Получение состояния плеера
    fun getStatePlayerLiveData(): LiveData<PlayerState> = statePlayerLiveData

    // Получение состояния кнопки Избранное
    fun getStateFavoritesButtonLiveData(): LiveData<Boolean> = stateFavoritesButtonLiveData


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
        player.setDataSource(url)
    }

    private fun preparePlayer() {
        player.prepareAsync()
    }

    private fun start() {
        player.start()
        statePlayerLiveData.postValue(PlayerState.Playing(currentPosition()))
        startTimer()
    }

    fun pause() {
        player.pause()
        timerJob?.cancel()
        statePlayerLiveData.postValue(PlayerState.Paused(currentPosition()))
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (player.isPlaying) {
                delay(REFRESH_PROGRESS_DELAY)
                if (statePlayerLiveData.value is PlayerState.Playing) {
                    statePlayerLiveData.postValue(PlayerState.Playing(currentPosition()))
                }
            }
        }
    }

    private fun currentPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(player.currentPosition)
    }

    private fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        player.setOnPreparedListener(listener)
    }

    private fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        player.setOnCompletionListener(listener)
    }

    private fun release () {
        player.release()
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }

    fun onFavoriteClicked(track: Track) {
        playerInteractor.onFavoriteClicked(track)
        track.isFavorite = !track.isFavorite
        stateFavoritesButtonLiveData.postValue(track.isFavorite)
    }
}
