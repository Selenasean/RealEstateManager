package com.openclassrooms.realestatemanager.utils.events

sealed interface CreationEvents {
    data object isCreated: CreationEvents

    data object isInternetAvailable: CreationEvents

    data object isUpdated : CreationEvents
}