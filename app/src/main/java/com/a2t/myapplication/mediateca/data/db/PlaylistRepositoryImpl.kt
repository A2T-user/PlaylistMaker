package com.a2t.myapplication.mediateca.data.db

import com.a2t.myapplication.mediateca.domain.api.PlaylistRepository
import com.a2t.myapplication.player.data.db.entity.TrackFromPlaylistsEntity
import com.a2t.myapplication.search.domain.models.Track
import com.a2t.myapplication.сreateplaylist.data.db.PlaylistDbConvertor
import com.a2t.myapplication.сreateplaylist.domain.model.Playlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PlaylistRepositoryImpl (
    private val appDatabase: AppDatabase,
    private val convertor: PlaylistDbConvertor,
): PlaylistRepository {

    override suspend fun getPlaylist(): Flow<List<Playlist>> {
        return appDatabase.getPlaylistDao().getPlaylists().map { entityList -> entityList.map { convertor.map(it) } }
    }

    override fun getPlaylistById(playlistId: Long): Flow<Playlist> = flow {
        val playlist = appDatabase.getPlaylistDao().getPlaylistById(playlistId)
        emit(convertor.map(playlist))
    }

    override fun getPlaylistTrackList(playlistIdList: List<Int>): Flow<List<Track>> {
        return appDatabase.getPlaylistDao().getListOfAllPlaylistTracks().map {listEntity -> trackFilter(listEntity, playlistIdList)}
    }

    private fun trackFilter (listEntity: List<TrackFromPlaylistsEntity>, playlistIdList: List<Int>): List<Track> {
        val result = mutableListOf<Track>()
        for (track: TrackFromPlaylistsEntity in listEntity) if (playlistIdList.any { it == track.trackId }) result.add(trackConverter(track))
        return result
    }
    private fun trackConverter (track: TrackFromPlaylistsEntity): Track {
        return Track (
            track.trackId,
            track.trackName,
            track.artistName,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.trackTime,
            track.artworkUrl100,
            track.artworkUrl512,
            track.previewUrl
        )
    }

    override fun deleteTrackById(trackId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.getPlaylistDao().deleteTrackById(trackId)
        }
    }

    override fun deletePlaylistById(playlistId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.getPlaylistDao().deletePlaylistById(playlistId)
        }
    }


}