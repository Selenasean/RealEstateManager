package com.openclassrooms.realestatemanager.data.model

import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.R

enum class Amenity(@StringRes val displayName: Int) {
    SCHOOL(R.string.school),
    SHOP(R.string.shop),
    SHOPPING_MALL(R.string.mall),
    STATION(R.string.station),
    BAKERY(R.string.bakery),
    GYM(R.string.gym),
    FOREST(R.string.forest),

}