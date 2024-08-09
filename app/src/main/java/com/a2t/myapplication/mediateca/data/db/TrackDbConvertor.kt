package com.a2t.myapplication.mediateca.data.db

import com.a2t.myapplication.mediateca.data.db.entity.TrackEntity
import com.a2t.myapplication.search.domain.models.Track

class TrackDbConvertor {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
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

    fun map(track: TrackEntity): Track {
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
            track.previewUrl
        )
    }
}