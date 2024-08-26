package com.a2t.myapplication.сreateplaylist.ui.view_model

import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a2t.myapplication.сreateplaylist.domain.api.CreatePlaylistInteractor
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import com.a2t.myapplication.сreateplaylist.ui.fragment.isCreatePlaylistFragmentFilled
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val interactor: CreatePlaylistInteractor
) : ViewModel() {

    private var playListName: String = ""
    private var playListDescription: String = ""
    private var playListUri: String = ""

    private var isPlaylistAdded = MutableLiveData(StatPlaylistAdded.NOTHING)
    init {
        isCreatePlaylistFragmentFilled = false
    }

    fun getPlaylistAdded(): LiveData<StatPlaylistAdded> = isPlaylistAdded

    // Добавление плейлиста
    fun addNewPlaylist (playListUri: String) {

        val playList = Playlist(
            0L,
            playListName,
            if (playListUri.isNotEmpty()) interactor.saveImageToPrivateStorage(playListUri.toUri()).toString() else null,
            if (playListDescription.isNotEmpty()) playListDescription else null,
            mutableListOf()
        )

        viewModelScope.launch(Dispatchers.IO) {
            interactor
                .addNewPlaylist(playList)
                .collect {
                    when  {
                        it > 0 -> isPlaylistAdded.postValue(StatPlaylistAdded.SUCCESS)
                        else -> isPlaylistAdded.postValue(StatPlaylistAdded.NOTHING)
                    }
                }
        }
        isCreatePlaylistFragmentFilled = false
    }

    fun setName (name: String) {
        playListName = name
        isFilled()
    }

    fun setDescription (description: String) {
        playListDescription = description
        isFilled()
    }

    fun setUri (uri: String?) {
        playListUri = uri.toString()
        isFilled()
    }


    fun isFilled() {
        isCreatePlaylistFragmentFilled = playListName.isNotEmpty() || playListDescription.isNotEmpty() || playListUri.isNotEmpty()
    }
}

enum class StatPlaylistAdded {
    SUCCESS,
    NOTHING
}