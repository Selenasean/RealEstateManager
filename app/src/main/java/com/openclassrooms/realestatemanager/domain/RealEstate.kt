package com.openclassrooms.realestatemanager.domain

import com.openclassrooms.realestatemanager.data.model.BuildingType

data class RealEstate(
    val id: Long,
    val title: String,
    val city : String,
    val priceTag: Float,
    val type: BuildingType,
)