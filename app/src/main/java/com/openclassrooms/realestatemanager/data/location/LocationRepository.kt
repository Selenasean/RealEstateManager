package com.openclassrooms.realestatemanager.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.openclassrooms.realestatemanager.AppApplication
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow

class LocationRepository(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) {
    companion object {
        private const val LOCATION_REQUEST_INTERVAL_MS = 10000L // 10 seconds
        private const val SMALLEST_DISPLACEMENT_THRESHOLD_METER = 10f // 10 meters
    }

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
    private fun isPermissionEnabled(): Boolean{
        return ContextCompat.checkSelfPermission(
            AppApplication.Companion.appContext,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
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

//    fun stopLocationRequest() {
//        callback?.let{
//            fusedLocationProviderClient.removeLocationUpdates(it)
//            callback = null
//        }
//    }

//    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
//    fun startLocationRequest() {
//        //if LocationCallback is null
//        if (callback == null) {
//            callback = object : LocationCallback() {
//                override fun onLocationResult(locationResult: LocationResult) {
//
//                    val location = locationResult.lastLocation ?: return
//                    //store latest version of the user's position
//                    locationFlow.value = location
//                }
//            }
//        }
//        //put the callback in FusedLocationProviderClient
//        callback?.let {
//            fusedLocationProviderClient.removeLocationUpdates(it)
//        }
//
//
//        fusedLocationProviderClient.requestLocationUpdates(
//            LocationRequest.Builder(
//                Priority.PRIORITY_HIGH_ACCURACY,
//                LOCATION_REQUEST_INTERVAL_MS
//            )
//                .setMinUpdateDistanceMeters(SMALLEST_DISPLACEMENT_THRESHOLD_METER)
//                .build(),
//            callback!!,  //call callback on the mainThread
//            looper
//        )
//    }

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


}

