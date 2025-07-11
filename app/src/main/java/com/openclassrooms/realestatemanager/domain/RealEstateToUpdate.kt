package com.openclassrooms.realestatemanager.domain

import android.os.Parcelable
import com.openclassrooms.realestatemanager.data.model.Amenity
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.Status
import kotlinx.parcelize.Parcelize
import java.time.Instant

class RealEstateToUpdate (
    val id: Long,
    val type: BuildingType,
    val photos: List<Photo>,
    val address: String,
    val city: String,
    val price: Float,
    val surface: Int,
    val rooms: Int,
    val bedrooms: Int,
    val bathrooms: Int,
    val description: String,
    val amenities : List<Amenity>,
    val status: Status,
    val dateOfSale: Instant?,
    val dateOfCreation : Instant,
    val agentId: Long,
    val photoChanges: PhotoChanges
)

@Parcelize
data class PhotoChanges(
    val deleted: List<String> = emptyList(),
    val created: List<Photo> = emptyList(),
    val updated: List<Photo> = emptyList()
) : Parcelable