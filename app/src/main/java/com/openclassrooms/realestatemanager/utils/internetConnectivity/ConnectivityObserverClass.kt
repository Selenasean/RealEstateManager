package com.openclassrooms.realestatemanager.utils.internetConnectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import com.openclassrooms.realestatemanager.AppApplication
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ConnectivityObserverClass : ConnectivityObserver {

    private val connectivityManager =
        AppApplication.Companion.appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    //Return boolean for internet connectivity
    override val isInternetConnected: Flow<Boolean>
        get() = callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    val connected = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        networkCapabilities.hasCapability(
                            NetworkCapabilities.NET_CAPABILITY_VALIDATED
                        )
                    } else {
                        val activeNetworkInfo = connectivityManager.activeNetworkInfo
                        activeNetworkInfo != null && activeNetworkInfo.isConnected
                    }
                    trySend(connected)
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    trySend(false)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    trySend(false)
                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    trySend(true)
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(callback)
            } else {
                val networkRequest = NetworkRequest.Builder().build()
                connectivityManager.registerNetworkCallback(
                    networkRequest,
                    callback
                )
            }
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }
}