package com.openclassrooms.realestatemanager.utils

import android.os.Parcelable
import com.openclassrooms.realestatemanager.domain.Photo
import kotlinx.parcelize.Parcelize

/**
 * State to display photo selected on screen
 */
@Parcelize
data class PhotoSelectedViewState(
    val id: String,
    val uri: String,
    val label: String,
) : Parcelable

// MAPPING FUNCTION HERE
fun Photo.toPhotoSelectedViewState(): PhotoSelectedViewState {
    return PhotoSelectedViewState(
        id = this.id,
        uri = this.urlPhoto,
        label = this.label
    )
}
