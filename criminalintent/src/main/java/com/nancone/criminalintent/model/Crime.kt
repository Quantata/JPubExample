package com.nancone.criminalintent.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Crime(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var date: Date = Date(),
    var isSolved: Boolean = false,
    var requiresPolice: Int = CrimeType.normalCrime,
    var suspect: String = "") {

    val photoFileName
        get() = "IMG_$id.jpg"
}

