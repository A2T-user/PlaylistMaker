package com.a2t.myapplication.player.domain.impl

import android.media.MediaPlayer
import com.a2t.myapplication.mediateca.data.db.TrackDbConvertor
import com.a2t.myapplication.mediateca.domaim.api.FavoritesTracksRepository
import com.a2t.myapplication.player.domain.api.PlayerRepository
import com.a2t.myapplication.player.domain.api.PlayerInteractor
import com.a2t.myapplication.search.domain.models.Track

class PlayerInteractorImpl (
    private val favoritesTracksRepository: FavoritesTracksRepository,
    private val trackDbConvertor: TrackDbConvertor,
    private val repository: PlayerRepository
): PlayerInteractor {
    override fun setDataSource(url: String?) {
        repository.setDataSource(url)
    }

    override fun preparePlayer() {
        repository.preparePlayer()
    }

    override fun start() {
        repository.start()
    }

    override fun pause() {
        repository.pause()
    }

    override fun currentPosition(): String {
        return repository.currentPosition()
    }

    override fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        repository.setOnPreparedListener(listener)
    }

    override fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        repository.setOnCompletionListener(listener)
    }

    override fun isPlaying(): Boolean {
        return repository.isPlaying()
    }

    override fun release() {
        repository.release()
    }

    override fun onFavoriteClicked(track: Track) {
        val trackEntit = trackDbConvertor.map(track)
        if (track.isFavorite) {
            favoritesTracksRepository.deleteTrack(trackEntit)
        } else {
            favoritesTracksRepository.insertTrack(trackEntit)
        }
    }
}