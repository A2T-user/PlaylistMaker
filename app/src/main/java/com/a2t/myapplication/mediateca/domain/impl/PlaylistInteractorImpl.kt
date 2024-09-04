package com.a2t.myapplication.mediateca.domain.impl

import com.a2t.myapplication.mediateca.domain.api.PlaylistRepository
import com.a2t.myapplication.mediateca.domain.db.PlaylistInteractor
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
): PlaylistInteractor {

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylist()
    }

    override fun deleteTrackById(trackId: Int) {
        repository.deleteTrackById(trackId)
    }


}