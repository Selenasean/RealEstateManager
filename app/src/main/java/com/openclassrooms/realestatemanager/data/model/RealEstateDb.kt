package com.openclassrooms.realestatemanager.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDateTime

@Entity(tableName = "realEstates",
    foreignKeys = [ForeignKey(
        entity = RealEstateAgent::class,
        parentColumns = arrayOf("uid"),
        childColumns = arrayOf("realEstateAgentId")
    )]
)
data class RealEstateDb(

    @PrimaryKey(autoGenerate = true) val uid: Long = 0,

    val type: BuildingType,
    val price: Float,
    val name : String,
    val surface: Int,
    val rooms: Int,
    val description: String,
    val address: String,
    val city : String,
    val nearbyBusiness : String?,
    val status: Status,
    val dateCreated: Instant,
    val dateOfSale: Instant?,
    val realEstateAgentId: Long
)
