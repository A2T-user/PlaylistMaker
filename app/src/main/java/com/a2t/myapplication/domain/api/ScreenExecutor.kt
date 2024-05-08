package com.a2t.myapplication.domain.api

import com.a2t.myapplication.domain.models.Track

interface ScreenExecutor {
    fun execute (track: Track?)
}