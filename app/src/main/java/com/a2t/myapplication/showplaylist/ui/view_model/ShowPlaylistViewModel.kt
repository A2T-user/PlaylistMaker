package com.a2t.myapplication.showplaylist.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a2t.myapplication.mediateca.domain.db.PlaylistInteractor
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.showplaylist.domain.db.ShowPlaylistInteractor
import com.a2t.myapplication.сreateplaylist.domain.api.CreatePlaylistInteractor
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShowPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val showPlaylistInteractor: ShowPlaylistInteractor,
    private val createPlaylistInteractor: CreatePlaylistInteractor
) : ViewModel() {

    private var playlist: Playlist? = null
    private var showPlaylistsLiveData = MutableLiveData<Playlist?>(playlist)

    fun getShowPlaylistsLiveData(): LiveData<Playlist?> = showPlaylistsLiveData

    fun getPlaylistById (playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            showPlaylistInteractor
                .getPlaylistById(playlistId)
                .collect { playlist ->
                    showPlaylistsLiveData.postValue(playlist)
                    showPlaylistInteractor
                        .getPlaylistTrackList(playlist.playlistIdList)
                        .collect { playlistTracksLiveData.postValue(it) }
                }

        }
    }

    fun getPlaylistTrackList(playlistIdList: List<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            showPlaylistInteractor
                .getPlaylistTrackList(playlistIdList)
                .collect { playlistTracksLiveData.postValue(it) }
        }


    }

    private var playlistTracksLiveData = MutableLiveData(listOf<Track>())

    fun getPlaylistTracksLiveData(): LiveData<List<Track>> = playlistTracksLiveData

    fun updatePlaylist(playlist: Playlist, track: Track) {
        val trackId = track.trackId
        playlist.playlistIdList.remove(trackId)

        viewModelScope.launch(Dispatchers.IO) {
            createPlaylistInteractor
                .updatePlaylist(playlist)
                .collect {}
        }
    }

    fun deleteTrack (trackId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor
                .getPlaylists()
                .collect {
                    if (trackCheckForDeletion(trackId, it)) {
                        playlistInteractor.deleteTrackById(trackId)         // Удалить трек
                    }
                }
        }
    }

    private fun trackCheckForDeletion (trackId: Int, list: List<Playlist>): Boolean {
        for (playlist: Playlist in list) {
            if (!playlist.playlistIdList.none { it == trackId }) return false
        }
        return true
    }


    fun deletePlaylistById(playlistId: Long, tracksId: List<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            showPlaylistInteractor.deletePlaylistById(playlistId)
            playlistInteractor
                .getPlaylists()
                .collect { playlists ->
                    // Создаем сводный список треков, используемых во всех плей листах
                    val masterPlaylist = mutableSetOf<Int>()
                    for (playlist: Playlist in playlists) {
                        masterPlaylist.addAll(playlist.playlistIdList)
                    }
                    for (trackId: Int in tracksId) {
                        if (masterPlaylist.none { it == trackId }) {
                            playlistInteractor.deleteTrackById(trackId)         // Удалить трек
                        }
                    }
                }
        }
    }
}