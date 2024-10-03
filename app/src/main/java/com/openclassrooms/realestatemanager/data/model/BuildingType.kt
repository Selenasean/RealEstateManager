package com.openclassrooms.realestatemanager.data.model

import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.R

enum class BuildingType(@StringRes val displayName: Int) {
    HOUSE(R.string.house),
    APARTMENT(R.string.apartment),
    VILA(R.string.vila),
}