package com.openclassrooms.realestatemanager.utils

sealed interface InternetEvent {

    data object NoInternetToast : InternetEvent

}