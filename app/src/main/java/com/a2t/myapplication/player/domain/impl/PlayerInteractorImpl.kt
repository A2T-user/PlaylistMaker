package com.a2t.myapplication.player.domain.impl

import com.a2t.myapplication.mediateca.data.db.TrackDbConvertor
import com.a2t.myapplication.mediateca.domaim.api.FavoritesTracksRepository
import com.a2t.myapplication.player.domain.api.PlayerInteractor
import com.a2t.myapplication.search.domain.models.Track

class PlayerInteractorImpl (
    private val favoritesTracksRepository: FavoritesTracksRepository,
    private val trackDbConvertor: TrackDbConvertor,
): PlayerInteractor {

    override fun onFavoriteClicked(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        if (track.isFavorite) {
            favoritesTracksRepository.deleteTrack(trackEntity)
        } else {
            favoritesTracksRepository.insertTrack(trackEntity)
        }
    }
}