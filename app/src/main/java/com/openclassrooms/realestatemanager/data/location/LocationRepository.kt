package com.openclassrooms.realestatemanager.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.openclassrooms.realestatemanager.AppApplication
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationRepository(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) {
    companion object {
        private const val LOCATION_REQUEST_INTERVAL_MS = 10000L // 10 seconds
        private const val SMALLEST_DISPLACEMENT_THRESHOLD_METER = 10f // 10 meters
    }

    private var _currentLocation: Location? = null
    private val looper: Looper = Looper.getMainLooper()

    //to know if location is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            AppApplication.appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    //To know if permission is given by user
    private fun isPermissionEnabled(): Boolean {
        return ContextCompat.checkSelfPermission(
            AppApplication.Companion.appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

//    private fun getLocation() {
//        if (isLocationEnabled() && isPermissionEnabled()) {
//            fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
//                val location: Location? = task.result
//                if (location != null) {
//                    //use the location latitude and logitude as per your use.
//                    val latitude = location.latitude
//                    val longitude = location.longitude
//                }
//            }
//        }


    @RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
    fun locationFlow(): Flow<Location> = callbackFlow {
        val callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {

                val location = locationResult.lastLocation ?: return
                //store latest version of the user's position
                trySend(location)
            }
        }
        //register callback
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                LOCATION_REQUEST_INTERVAL_MS
            )
                .setMinUpdateDistanceMeters(SMALLEST_DISPLACEMENT_THRESHOLD_METER)
                .build(),
            callback,  //call callback on the mainThread
            looper
        )
        //when Flow closed execute lambda
        awaitClose { fusedLocationProviderClient.removeLocationUpdates(callback) }
    }


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
            }
                .addOnCanceledListener {
                    continuation.resume(null)
                }


        }

    }


}
