package com.openclassrooms.realestatemanager.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "photos",
    foreignKeys = [ForeignKey(
        entity = RealEstateDb::class,
        parentColumns = ["uid"],
        childColumns = ["realEstateId"]
    )]
)
data class PhotoDb(
    @PrimaryKey val uid: String,

    val realEstateId: Long,
    val urlPhoto : String,
)
