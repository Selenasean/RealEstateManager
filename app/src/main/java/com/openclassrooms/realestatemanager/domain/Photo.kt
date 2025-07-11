package com.openclassrooms.realestatemanager.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Photo(
    val id: String,
    val urlPhoto :String,
    val label: String
) : Parcelable
