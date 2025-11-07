package com.openclassrooms.realestatemanager.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "photos",
    foreignKeys = [ForeignKey(
        entity = RealEstateDb::class,
        parentColumns = ["id"],
        childColumns = ["realEstateId"]
    )]
)
data class PhotoDb(
    @PrimaryKey val id: String,

    val realEstateId: Long,
    val urlPhoto : String,
    val label: String
)

//TODO : delete