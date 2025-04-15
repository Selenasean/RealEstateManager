package com.openclassrooms.realestatemanager.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "realEstates",
    foreignKeys = [ForeignKey(
        entity = RealEstateAgentDb::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("realEstateAgentId")
    )]
)
data class RealEstateDb(

    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    val type: BuildingType,
    val price: Float,
    val name : String,
    val surface: Int,
    val rooms: Int,
    val bathrooms : Int,
    val bedrooms : Int,
    val description: String,
    val address: String,
    val city : String,
    val status: Status,
    val amenities: List<Amenity>,
    val dateCreated: Instant,
    val dateOfSale: Instant?,
    val realEstateAgentId: Long,
    val longitude: Double,
    val latitude : Double
)
