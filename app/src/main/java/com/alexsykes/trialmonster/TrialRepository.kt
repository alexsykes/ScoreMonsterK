package com.alexsykes.trialmonster

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TrialRepository(private val trialDao: TrialDao) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    val allTrials: LiveData<List<Trial>> = trialDao.getAllTrials()
    val searchResults = MutableLiveData<List<Trial>> ()

    fun insertTrial(trial: Trial) {
        coroutineScope.launch ( Dispatchers.IO ) {
            trialDao.insertTrial(trial)
        }
    }

    fun deleteTrial(name: String) {
        coroutineScope.launch(Dispatchers.IO) {
            trialDao.deleteTrial(name)
        }
    }

    fun findTrial(name: String) {
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = asyncFind(name).await()
        }
    }

    private fun asyncFind(name: String): Deferred<List<Trial>?> =
        coroutineScope.async  (Dispatchers.IO) {
            return@async trialDao.findTrial(name)
        }
}