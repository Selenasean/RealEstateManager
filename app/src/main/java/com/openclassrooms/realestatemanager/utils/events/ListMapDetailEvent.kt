package com.openclassrooms.realestatemanager.utils.events

import android.location.Location

sealed interface ListMapDetailEvent {
    data object OpenDetails : ListMapDetailEvent

}

sealed interface MapEvent{
    data class CenterUserLocation(val location: Location) : MapEvent

}