package com.openclassrooms.realestatemanager.domain

import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.Status

data class Filters(
    val city: String?,
    val type : List<BuildingType>,
    val priceMin : Int?,
    val priceMax: Int?,
    val surfaceMin : Int?,
    val surfaceMax: Int?,
    val status: Status?,
<<<<<<< HEAD
)

=======
){
    fun isNoFilters(): Boolean{
        return city == null &&
                type.isEmpty() &&
                priceMin == null &&
                priceMax == null &&
                surfaceMin == null &&
                surfaceMax== null &&
                status == null
    }
}
>>>>>>> 8bf0f29354f3eef7575b8ca55e26e60015b301c0

