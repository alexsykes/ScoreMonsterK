package com.alexsykes.trialmonster

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel(application: Application) : ViewModel() {
    val allTrials: LiveData<List<Trial>>
    private val repository: TrialRepository
    val searchResults: MutableLiveData<List<Trial>>

    init {
        val trialDb= TrialRoomDatabase.getInstance(application)
        val trialDao = trialDb.trialDao()
        repository = TrialRepository(trialDao)

        allTrials = repository.allTrials
        searchResults = repository.searchResults
    }

    fun insertTrial(trial: Trial) {
        repository.insertTrial(trial)
    }

    fun findTrial(name: String) {
        repository.findTrial(name)
    }

    fun deleteTrial(name: String) {
        repository.deleteTrial(name)
    }
}