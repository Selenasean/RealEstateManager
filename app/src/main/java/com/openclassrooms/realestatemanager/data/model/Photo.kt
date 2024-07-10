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
data class Photo(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0 ,

    val realEstateId: Long,
    val urlPhoto : String,
)
