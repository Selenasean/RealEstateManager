package com.openclassrooms.realestatemanager.data

import android.content.Context
import android.location.Address
import android.location.Geocoder

class GeocoderRepository(context: Context) {

    val geocoder: Geocoder = Geocoder(context)

    fun getLongLat(address: String): Position? {
        // return null si geocoder return qlqch de null sinon assignation de la valeur à la variable locations
       try{
           val location: Address = geocoder.getFromLocationName(address, 1)?.firstOrNull() ?: return null

           return Position(
               latitude = location.latitude,
               longitude = location.longitude
           )
       }catch(exception: Exception){
           return null
       }

    }


}


data class Position(
    val latitude: Double,
    val longitude: Double
)