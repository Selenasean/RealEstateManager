package com.openclassrooms.realestatemanager.utils.internetConnectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val isInternetConnected: Flow<Boolean>
}
