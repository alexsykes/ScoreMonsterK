package com.alexsykes.trialmonster

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "trials")
class Trial {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "trialId")
    var id: Int = 0

    var date: String = ""
    var trialName: String = ""
    var club: String = ""
    var resultsPublished: Boolean = false
    var location: String = ""



    constructor()  {}

    constructor(date: String, trialName: String, club: String) {
        this.date = date
        this.trialName = trialName
        this.club = club
    }

    constructor( trialName: String, club: String) {
        this.date = Date().toString()
        this.trialName = trialName
        this.club = club

    }
}