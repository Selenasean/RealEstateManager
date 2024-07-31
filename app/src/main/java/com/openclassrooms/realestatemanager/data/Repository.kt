package com.openclassrooms.realestatemanager.data

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
            RealEstate(
                id = entry.key.uid,
                title = entry.key.name,
                city = entry.key.city,
                priceTag = entry.key.price,
                type = entry.key.type,
                photos = entry.value.map { photoDb ->
                    Photo(
                        id = photoDb.uid,
                        urlPhoto = photoDb.urlPhoto,
                    )
                }

            )

        }

    }

    //TODO : function mapping
}