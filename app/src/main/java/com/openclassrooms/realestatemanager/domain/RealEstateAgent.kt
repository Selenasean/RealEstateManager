package com.openclassrooms.realestatemanager.domain

import android.os.Parcelable
import com.openclassrooms.realestatemanager.data.model.RealEstateAgentDb
import kotlinx.parcelize.Parcelize

@Parcelize
data class RealEstateAgent(
    val id: Long,
    val name: String,
) : Parcelable

//MAPPING FUNCTION HERE
fun RealEstateAgentDb.toRealEstateAgent(): RealEstateAgent {
    return RealEstateAgent(
        id = this.id,
        name = this.name
    )
}
