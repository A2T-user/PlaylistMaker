package com.a2t.myapplication.mediateca.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a2t.myapplication.mediateca.domaim.db.PlaylistInteractor
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistViewModel (
    private val playlistInteractor: PlaylistInteractor

): ViewModel() {

    private var playlistsLiveData = MutableLiveData(listOf<Playlist>())

    fun getPlaylists () {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor
                .getPlaylists()
                .collect { playlistsLiveData.postValue(it) }
        }
    }

    fun getPlaylistsLiveData(): LiveData<List<Playlist>> = playlistsLiveData
}
