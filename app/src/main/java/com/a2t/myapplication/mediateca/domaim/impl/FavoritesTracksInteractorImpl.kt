package com.a2t.myapplication.mediateca.domaim.impl

//import com.a2t.myapplication.mediateca.data.db.TrackDbConvertor
import com.a2t.myapplication.mediateca.domaim.api.FavoritesTracksRepository
import com.a2t.myapplication.mediateca.domaim.db.FavoritesTracksInteractor
import com.a2t.myapplication.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesTracksInteractorImpl(
    private val repository: FavoritesTracksRepository/*,
    private val trackDbConvertor: TrackDbConvertor*/
): FavoritesTracksInteractor {

    /*override fun insertTrack(track: Track) {
        val trackEntit = trackDbConvertor.map(track)
        repository.insertTrack(trackEntit)
    }

    override fun deleteTrack(track: Track) {
        val trackEntit = trackDbConvertor.map(track)
        repository.deleteTrack(trackEntit)
    }*/

    override fun getTracks(): Flow<List<Track>> {
        return repository.getTracks().map { it.reversed() }
    }
}