package com.openclassrooms.realestatemanager.utils.events

sealed interface InternetEvent {

    data object NoInternetToast : InternetEvent

}