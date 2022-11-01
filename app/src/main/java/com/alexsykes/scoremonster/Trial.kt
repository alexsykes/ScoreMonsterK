package com.alexsykes.scoremonster

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trials")
class Trial {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "trialId")
    var id: Int = 0


    var trialName: String = ""
    var club: String = ""

    constructor()  {}

    constructor(trialName: String, club: String) {
        this.trialName = trialName
        this.club = club
    }

    constructor(id: Int, trialName: String, club: String) {
        this.trialName = trialName
        this.club = club
    }
}