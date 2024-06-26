package com.openclassrooms.realestatemanager.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.DateTimeException
import java.time.LocalDateTime

@Entity(tableName = "realEstates",
    foreignKeys = [ForeignKey(
        entity = RealEstateAgent::class,
        parentColumns = arrayOf("uid"),
        childColumns = arrayOf("realEstateAgentId")
    )]
)
data class RealEstate(

    @PrimaryKey(autoGenerate = true) val uid: Long = 0 ,

    val type: BuildingType,
    val price: Float,
//    val surface: Int,
//    val rooms: Int,
//    val description: String,
//    val address: String,
//    val nearbyBusiness : String?,
//    val status: Status,
//    val dateCreated: LocalDateTime,
//    val dateOfSale: LocalDateTime?,
    val realEstateAgentId: Long
)
