package com.openclassrooms.realestatemanager.data

import android.content.Context
import android.location.Address
import android.location.Geocoder

class GeocoderRepository(context: Context) {

    val geocoder: Geocoder = Geocoder(context)

    fun getLongLat(address: String): Position? {
//        geocoder.getFromLocationName( address, 1, object : GeocodeListener {
//            override fun onGeocode(addresses: MutableList<Address>) {
//                TODO("Not yet implemented")
//            }
//        })
        // return null si geocoder return qlqch de null sinon assignation de la valeur à la variable locations
        val location: Address =
            geocoder.getFromLocationName(address, 1)?.firstOrNull() ?: return null
        
        return Position(
            latitude = location.latitude,
            longitude = location.longitude
        )
    }


}


data class Position(
    val latitude: Double,
    val longitude: Double
)