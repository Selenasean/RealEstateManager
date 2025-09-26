package com.openclassrooms.realestatemanager.domain

import android.os.Parcelable
import com.openclassrooms.realestatemanager.data.model.PhotoDb
import kotlinx.parcelize.Parcelize

@Parcelize
data class Photo(
    val id: String,
    val urlPhoto :String,
    val label: String
) : Parcelable

// MAPPING FUNCTION HERE
fun Photo.toPhotoDb(realEstateId: Long): PhotoDb {
    return PhotoDb(
        id = this.id,
        realEstateId = realEstateId,
        urlPhoto = this.urlPhoto,
        label = this.label
    )
}