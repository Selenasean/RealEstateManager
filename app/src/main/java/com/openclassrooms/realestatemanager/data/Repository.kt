package com.openclassrooms.realestatemanager.data

import com.openclassrooms.realestatemanager.domain.RealEstate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository {

    private val realEstateList: List<RealEstate> = listOf(
        RealEstate("0", "Pistache", 1000),
        RealEstate("1", "Fraise", 1100),
        RealEstate("2", "Ananas", 2000),
        RealEstate("3", "Fruit de la Passion", 12000),
        RealEstate("4", "Pomme", 800),
        RealEstate("5", "Durian", 9000)
    )

    suspend fun getList(): List<RealEstate>{

        return realEstateList
    }

    suspend fun getOne(id: String): RealEstate?{
        return realEstateList.find { realEstate -> realEstate.id == id }
    }

     fun getAllRealEstates(): Flow<List<RealEstate>> {

        return flow { emit(realEstateList) }
    }

    fun getOneRealEstate(id: String): Flow<RealEstate?>{
        val estate = realEstateList.find { realEstate -> realEstate.id == id }
        return flow { emit(estate) }
    }



}