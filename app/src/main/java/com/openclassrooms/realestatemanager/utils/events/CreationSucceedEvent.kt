package com.openclassrooms.realestatemanager.utils.events

sealed interface CreationSucceedEvent {
    data object isCreated: CreationSucceedEvent
}