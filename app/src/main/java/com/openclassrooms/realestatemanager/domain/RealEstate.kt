package com.openclassrooms.realestatemanager.domain

import com.openclassrooms.realestatemanager.data.model.Amenity
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.Status

data class RealEstate(
    val id: String,
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
    val address : String,
    val status : Status,
    val amenities : List<Amenity>,
    val latitude: Double?,
    val longitude: Double?

)