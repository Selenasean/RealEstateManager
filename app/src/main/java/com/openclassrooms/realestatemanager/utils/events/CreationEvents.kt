package com.openclassrooms.realestatemanager.utils.events

/**
 * Interface to catch Events
 */
sealed interface CreationEvents {
    data object isCreated: CreationEvents

    data object isInternetAvailable: CreationEvents

    data object isUpdated : CreationEvents

    data object failure : CreationEvents
}