package com.openclassrooms.realestatemanager.ui.filter

import android.os.Parcelable
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.Status
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterState (
    val city: String? = null,
    val type : List<BuildingType> = emptyList(),
    val priceMin : Int? = null,
    val priceMax: Int? = null,
    val surfaceMin : Int? = null,
    val surfaceMax: Int? = null,
    val status: Status? = null,
) : Parcelable

