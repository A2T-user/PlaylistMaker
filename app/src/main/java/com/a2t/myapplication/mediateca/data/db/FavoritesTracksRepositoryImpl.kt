package com.a2t.myapplication.mediateca.data.db

import com.a2t.myapplication.mediateca.data.db.entity.TrackEntity
import com.a2t.myapplication.mediateca.domaim.api.FavoritesTracksRepository
import com.a2t.myapplication.search.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class FavoritesTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : FavoritesTracksRepository {

    override fun insertTrack(track: TrackEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.getTrackDao().insertTrack(track)
        }
    }

    override fun deleteTrack(track: TrackEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.getTrackDao().deleteTrack(track)
        }
    }

    override fun getTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.getTrackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { movie -> trackDbConvertor.map(movie) }
    }

}