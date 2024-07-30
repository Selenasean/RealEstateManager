package com.openclassrooms.realestatemanager.data.model

import com.openclassrooms.realestatemanager.R

enum class Status(state: String) {
    SOLD(R.string.sold.toString()),
    ON_SALE(R.string.on_sale.toString()),
    FOR_SALE(R.string.for_sale.toString())
}
