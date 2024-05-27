package com.a2t.myapplication.search.ui.models

import com.a2t.myapplication.search.domain.models.Track

data class SearchData (
    val screenMode: FilterScreenMode,
    val foundTracks: List<Track>?,
    val errorMessage: String?
)
