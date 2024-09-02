package com.a2t.myapplication.mediateca.data.db

import com.a2t.myapplication.mediateca.domaim.api.PlaylistRepository
import com.a2t.myapplication.сreateplaylist.data.db.PlaylistDbConvertor
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl (
    private val appDatabase: AppDatabase,
    private val convertor: PlaylistDbConvertor,
): PlaylistRepository {

    override suspend fun getPlaylist(): Flow<List<Playlist>> {
        return appDatabase.getPlaylistDao().getPlaylists().map { entityList -> entityList.map { convertor.map(it) } }
    }
}