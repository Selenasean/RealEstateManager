package com.openclassrooms.realestatemanager.utils.events

sealed interface EventDetailPane {
    data object OpenDetails : EventDetailPane

}