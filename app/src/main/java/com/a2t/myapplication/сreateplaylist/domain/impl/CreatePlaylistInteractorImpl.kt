package com.a2t.myapplication.сreateplaylist.domain.impl

import com.a2t.myapplication.player.data.db.TrackFromPlaylistsEntityDbConvertor
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.сreateplaylist.data.db.PlaylistDbConvertor
import com.a2t.myapplication.сreateplaylist.domain.api.CreatePlaylistInteractor
import com.a2t.myapplication.сreateplaylist.domain.db.CreatePlaylistRepository
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import kotlinx.coroutines.flow.Flow


class CreatePlaylistInteractorImpl (
    private val repository: CreatePlaylistRepository,
    private val playlistConvertor: PlaylistDbConvertor
): CreatePlaylistInteractor {

    override fun addNewPlaylist(playlist: Playlist): Flow<Long> {
        val playlistEntity = playlistConvertor.map(playlist)
        return repository.addNewPlaylist(playlistEntity)
    }

    override fun updatePlaylist(playlist: Playlist): Flow<Int> {
        val playlistEntity = playlistConvertor.map(playlist)
        return repository.updatePlaylist(playlistEntity)
    }


    override fun saveImageToPrivateStorage(uri: String): String {
        return repository.saveImageToPrivateStorage(uri)
    }

    override fun addTrackInPlaylist(track: Track) {
        val trackFromPlaylistsEntity = TrackFromPlaylistsEntityDbConvertor().map(track)
        repository.addTrackInPlaylist(trackFromPlaylistsEntity)
    }
}