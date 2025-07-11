package com.openclassrooms.realestatemanager.ui.create_edit

import android.os.Parcelable
import com.openclassrooms.realestatemanager.data.model.Amenity
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.domain.RealEstateAgent
import com.openclassrooms.realestatemanager.utils.PhotoSelectedViewState
import kotlinx.parcelize.Parcelize
import java.time.Instant

/**
 * State to store data & enabled creation button
 */
@Parcelize
data class RealEstateToSaveState(
    val type: BuildingType? = null,
    val address: String? = null,
    val city: String? = null,
    val price: String? = null,
    val surface: String? = null,
    val rooms: String? = null,
    val bedrooms: String? = null,
    val bathrooms: String? = null,
    val description: String? = null,
    val amenities: List<Amenity> = emptyList(),
    val agent: RealEstateAgent? = null,
    val status: Status = Status.FOR_SALE,
    val dateOfSale: Instant? = null,
    val dateOfCreation: Instant? = null,
    val photos: List<PhotoSelectedViewState> = emptyList(),
) : Parcelable {

    fun isInputsCompleted(): Boolean {
        return listOf(
            type.toString(),
            address,
            city,
            price,
            surface,
            rooms,
            bedrooms,
            bathrooms,
            description,
        ).none { it.isNullOrBlank() }
                && agent != null
                && photos.isNotEmpty()
    }
}