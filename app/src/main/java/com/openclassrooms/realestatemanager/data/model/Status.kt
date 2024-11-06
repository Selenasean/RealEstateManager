package com.openclassrooms.realestatemanager.data.model

import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.R

enum class Status(@StringRes val state: Int) {
    SOLD(R.string.sold),
    FOR_SALE(R.string.for_sale)
}
