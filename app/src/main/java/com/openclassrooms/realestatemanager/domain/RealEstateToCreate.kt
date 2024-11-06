package com.openclassrooms.realestatemanager.domain

import com.openclassrooms.realestatemanager.data.model.Amenity
import com.openclassrooms.realestatemanager.data.model.BuildingType

data class RealEstateToCreate(
    val type: BuildingType,
    val address: String,
    val city: String,
    val price: Float,
    val surface: Int,
    val rooms: Int,
    val bedrooms: Int,
    val bathrooms: Int,
    val description: String,
    val amenities : List<Amenity>,
    val agentId: Long
)