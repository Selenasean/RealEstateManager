package com.openclassrooms.realestatemanager.domain


import com.openclassrooms.realestatemanager.data.model.Amenity
import com.openclassrooms.realestatemanager.data.model.BuildingType

import com.openclassrooms.realestatemanager.data.model.PhotoDb
import com.openclassrooms.realestatemanager.data.model.RealEstateDb
import com.openclassrooms.realestatemanager.data.model.Status
import java.time.Instant

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
    val latitude: Double,
    val longitude: Double,
    val agentId: Long,
    val dateCreated : Instant,
    val dateOfSale : Instant?,
    val video: String?
)

//MAPPING FUNCTION
fun Map<RealEstateDb, List<PhotoDb>>.toRealEstate(): List<RealEstate> {
    return this.entries.map { entry ->
        val photos: List<PhotoDb> = entry.value
        RealEstate(
            id = entry.key.id.toString(),
            title = entry.key.name,
            city = entry.key.city,
            priceTag = entry.key.price,
            type = entry.key.type,
            photos = photos.map { photoDb ->
                Photo(
                    id = photoDb.id,
                    urlPhoto = photoDb.urlPhoto,
                    label = photoDb.label
                )
            },
            surface = entry.key.surface,
            rooms = entry.key.rooms,
            bathrooms = entry.key.bathrooms,
            bedrooms = entry.key.bedrooms,
            description = entry.key.description,
            address = entry.key.address,
            status = entry.key.status,
            amenities = entry.key.amenities,
            latitude = entry.key.latitude,
            longitude = entry.key.longitude,
            agentId = entry.key.realEstateAgentId,
            dateCreated = entry.key.dateCreated,
            dateOfSale = entry.key.dateOfSale,
            video = entry.key.video
        )
    }
}