package com.a2t.myapplication.сreateplaylist.domain.impl

import com.a2t.myapplication.сreateplaylist.data.db.PlaylistDbConvertor
import com.a2t.myapplication.сreateplaylist.domain.api.CreatePlaylistInteractor
import com.a2t.myapplication.сreateplaylist.domain.db.CreatePlaylistRepository
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import kotlinx.coroutines.flow.Flow


class CreatePlaylistInteractorImpl (
    private val repository: CreatePlaylistRepository,
    private val convertor: PlaylistDbConvertor
): CreatePlaylistInteractor {

    override fun addNewPlaylist(playlist: Playlist): Flow<Long> {
        val playlistEntity = convertor.map(playlist)
        return repository.addNewPlaylist(playlistEntity)
    }

    override fun updatePlaylist(playlist: Playlist): Flow<Int> {
        val playlistEntity = convertor.map(playlist)
        return repository.updatePlaylist(playlistEntity)
    }


    override fun saveImageToPrivateStorage(uri: String): String {
        return repository.saveImageToPrivateStorage(uri)
    }


}