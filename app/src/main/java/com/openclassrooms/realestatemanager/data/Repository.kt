package com.openclassrooms.realestatemanager.data

import android.util.Log
import com.openclassrooms.realestatemanager.data.model.PhotoDb
import com.openclassrooms.realestatemanager.domain.Photo
import com.openclassrooms.realestatemanager.domain.RealEstate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Repository(private val appdatabase: AppDataBase) {


    /**
     * Get all RealEstates
     */
    fun getAllRealEstates(): Flow<List<RealEstate>> {

        return appdatabase.realEstateDao().getAllRealEstates().map { realEstateDbs ->
            realEstateDbs.map {
                RealEstate(
                    id = it.key.uid,
                    title = it.key.name,
                    city = it.key.city,
                    priceTag = it.key.price,
                    type = it.key.type,
                    photos = it.value.map { photoDb ->
                        Photo(
                            photoDb.uid,
                            photoDb.urlPhoto
                        )
                    },
                    surface = it.key.surface,
                    rooms = it.key.rooms,
                    bathrooms = it.key.bathrooms,
                    bedrooms = it.key.bedrooms,
                    description = it.key.description,
                    address = it.key.address
                )
            }
        }
    }

    /**
     * Get One RealEstates
     */
    fun getOneRealEstates(realEstateId: Long): Flow<RealEstate> {
        return appdatabase.realEstateDao().getOneRealEstate(realEstateId).map { realEstateDb ->
            val entry = realEstateDb.entries.first()
            val photos : List<PhotoDb> = entry.value
            RealEstate(
                id = entry.key.uid,
                title = entry.key.name,
                city = entry.key.city,
                priceTag = entry.key.price,
                type = entry.key.type,
                photos = photos.map { photoDb ->
                    Log.i("repository", "getOneRealEstate ${photoDb.urlPhoto}")
                    Photo(
                        id = photoDb.uid,
                        urlPhoto = photoDb.urlPhoto,
                    )
                },
                surface = entry.key.surface,
                rooms = entry.key.rooms,
                bathrooms = entry.key.bathrooms,
                bedrooms = entry.key.bedrooms,
                description = entry.key.description,
                address = entry.key.address

            )

        }

    }

    //TODO : function mapping
}