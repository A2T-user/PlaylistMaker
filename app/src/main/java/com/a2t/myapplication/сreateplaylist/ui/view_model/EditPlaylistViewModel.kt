package com.a2t.myapplication.сreateplaylist.ui.view_model

import androidx.lifecycle.viewModelScope
import com.a2t.myapplication.сreateplaylist.domain.api.CreatePlaylistInteractor
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import com.a2t.myapplication.сreateplaylist.ui.fragment.CreatePlaylistFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    interactor: CreatePlaylistInteractor
) : CreatePlaylistViewModel(interactor) {

    override fun isFilled() {}

    // Добавление плейлиста
    fun savePlaylist (oldPlaylistId: Long?, oldPlaylistUri: String?, oldPlaylistIdList: MutableList<Int>?) {
        // Создание обновленного объекта Playlist
        val playList = Playlist(
            oldPlaylistId!!,
            playListName,
            if (playListUri.isNotEmpty()) interactor.saveImageToPrivateStorage(playListUri) else oldPlaylistUri,
            playListDescription.ifEmpty { null },
            oldPlaylistIdList ?: mutableListOf()
        )

        viewModelScope.launch(Dispatchers.IO) {
            interactor
                .updatePlaylist(playList)
                .collect {}
        }
        CreatePlaylistFragment.isCreatePlaylistFragmentFilled = false
    }
}