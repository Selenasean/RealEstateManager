package com.openclassrooms.realestatemanager.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RealEstateAgent(
    val id: Long,
    val name: String,
) : Parcelable
