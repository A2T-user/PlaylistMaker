package com.a2t.myapplication.mediateca.domaim.impl

import com.a2t.myapplication.mediateca.domaim.api.PlaylistRepository
import com.a2t.myapplication.mediateca.domaim.db.PlaylistInteractor
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
): PlaylistInteractor {

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylist()
    }


}