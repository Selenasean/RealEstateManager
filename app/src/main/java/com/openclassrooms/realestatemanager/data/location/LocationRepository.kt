package com.openclassrooms.realestatemanager.data.location

import android.Manifest
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationRepository(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) {

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun getLastLocation(): Location? {
        //get callback in coroutines world
        return suspendCancellableCoroutine { continuation ->

            fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(task.result)
                } else {
                    continuation.resumeWithException(task.exception!!)
                }
            }.addOnCanceledListener {
                    continuation.resume(null)
                }


        }

    }


}
