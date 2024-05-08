package com.a2t.myapplication.data.network

import com.a2t.myapplication.domain.api.TrackProvider
import com.a2t.myapplication.domain.models.Track

class TrackProviderImpl (repository: TrekRepository): TrackProvider {
    private val rep = repository
    override fun getTrack(): Track? {
        return rep.getTrack()
    }

}