package com.openclassrooms.realestatemanager.domain

import com.openclassrooms.realestatemanager.data.model.BuildingType

data class RealEstate(
    val id: Long,
    val isSelected : Boolean = false,
    val title: String,
    val city : String,
    val priceTag: Float,
    val type: BuildingType,
    val photos : List<Photo>,
    val surface : Int,
    val rooms : Int,
    val bathrooms : Int,
    val bedrooms : Int,
    val description : String,
    val address : String

)