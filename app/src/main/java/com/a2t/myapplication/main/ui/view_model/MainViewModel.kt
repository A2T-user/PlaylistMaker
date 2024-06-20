package com.a2t.myapplication.main.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel()  {

    private val state: MainActivityState = MainActivityState.NOTHING
    private var stateLiveData = MutableLiveData(state)

    fun getStateLiveData(): LiveData<MainActivityState> = stateLiveData

    fun clickSearch () {
        stateLiveData.postValue(MainActivityState.SEARCH)
    }

    fun clickMediateca () {
        stateLiveData.postValue(MainActivityState.MEDIATECA)
    }

    fun clickSettings () {
        stateLiveData.postValue(MainActivityState.SETTINGS)
    }

    fun nothing () {
        stateLiveData.postValue(MainActivityState.NOTHING)
    }
}