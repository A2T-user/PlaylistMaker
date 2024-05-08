package com.a2t.myapplication.data.network

import com.a2t.myapplication.domain.models.Track

interface TrekRepository {
    fun getTrack (): Track?
}