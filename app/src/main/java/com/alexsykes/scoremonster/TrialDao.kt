package com.alexsykes.scoremonster

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TrialDao {

    @Insert
    fun insertTrial(trial: Trial)

    @Query("SELECT * FROM trials WHERE trialName = :name")
    fun findTrial(name: String): List<Trial>

    @Query("DELETE FROM trials WHERE trialName = :name")
    fun deleteTrial(name: String)

    @Query("SELECT * FROM trials ORDER BY trialName")
    fun getAllTrials(): LiveData<List<Trial>>
}