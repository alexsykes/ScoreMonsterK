package com.alexsykes.scoremonster

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [(Trial::class)], version = 1)
abstract class TrialRoomDatabase: RoomDatabase() {

    abstract fun trialDao(): TrialDao

    companion object{
        private var INSTANCE: TrialRoomDatabase? = null

        fun getInstance(context: Context): TrialRoomDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TrialRoomDatabase::class.java,
                        "trial_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}