package com.openclassrooms.realestatemanager.data

import com.openclassrooms.realestatemanager.data.model.RealEstateDb
import com.openclassrooms.realestatemanager.domain.RealEstate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class Repository(private val appdatabase : AppDataBase) {


    /**
     * Get all RealEstates
     */
    fun getAllRealEstates() : Flow<List<RealEstate>>{

        return  appdatabase.realEstateDao().getAllRealEstates().map {realEstateDbs ->
            realEstateDbs.map {
                RealEstate(
                    id = it.uid,
                    title = it.name,
                    city = it.city,
                    priceTag = it.price,
                    type = it.type
                )
            }
        }
    }

    /**
     * Get One RealEstates
     */
    fun getOneRealEstates(realEstateId : Long) : Flow<RealEstate>{
        return appdatabase.realEstateDao().getOneRealEstate(realEstateId).map{ realEstateDb ->
            RealEstate(
                realEstateDb.uid,
                realEstateDb.name,
                realEstateDb.city,
                realEstateDb.price,
                realEstateDb.type
            )
        }

    }

}