package com.a2t.myapplication.showplaylist.domain.impl

import com.a2t.myapplication.mediateca.domain.api.PlaylistRepository
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.showplaylist.domain.db.ShowPlaylistInteractor
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

class ShowPlaylistInteractorImpl(
    private val repository: PlaylistRepository
): ShowPlaylistInteractor {

    override fun getPlaylistById(playlistId: Long): Flow<Playlist> {
        return repository.getPlaylistById(playlistId)
    }

    override fun getPlaylistTrackList(playlistIdList: List<Int>): Flow<List<Track>> {
        return repository.getPlaylistTrackList(playlistIdList)
    }

    override fun deletePlaylistById(playlistId: Long) {
        repository.deletePlaylistById(playlistId)
    }
}