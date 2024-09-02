package com.a2t.myapplication.player.data.db

import com.a2t.myapplication.player.data.db.entity.TrackFromPlaylistsEntity
import com.a2t.myapplication.search.domain.models.Track

class TrackFromPlaylistsEntityDbConvertor {
    fun map(track: Track): TrackFromPlaylistsEntity {
        return TrackFromPlaylistsEntity(
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

    fun map(track: TrackFromPlaylistsEntity): Track {
        return Track(
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
            track.previewUrl,
        )
    }
}