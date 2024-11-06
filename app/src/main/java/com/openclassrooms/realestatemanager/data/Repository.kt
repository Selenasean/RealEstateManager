package com.openclassrooms.realestatemanager.data

import android.util.Log
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.PhotoDb
import com.openclassrooms.realestatemanager.data.model.RealEstateDb
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.domain.Photo
import com.openclassrooms.realestatemanager.domain.RealEstate
import com.openclassrooms.realestatemanager.domain.RealEstateAgent
import com.openclassrooms.realestatemanager.domain.RealEstateToCreate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

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
                    address = it.key.address,
                    status = it.key.status,
                    amenities = it.key.amenities,
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
                address = entry.key.address,
                status = entry.key.status,
                amenities = entry.key.amenities,
            )

        }

    }

    /**
     * To get all Agents
     */
    fun getAllAgents(): Flow<List<RealEstateAgent>> {
        return appdatabase.realEstateAgentDao().getAllAgents().map { realEstateAgentDb ->
            realEstateAgentDb.map{
                RealEstateAgent(
                    id = it.uid,
                    name = it.name
                )
            }
        }

    }

    fun createRealEstate(realEstate: RealEstateToCreate){

        appdatabase.realEstateDao().createRealEstate(
            RealEstateDb(
                type= realEstate.type,
                price= realEstate.price,
                name = "",
                surface = realEstate.surface,
                rooms = realEstate.rooms,
                bedrooms = realEstate.bedrooms,
                bathrooms = realEstate.bathrooms,
                description = realEstate.description,
                address = realEstate.description,
                city = realEstate.city,
                status = Status.FOR_SALE,
                amenities = realEstate.amenities,
                dateCreated = Instant.now(),
                dateOfSale = null,
                realEstateAgentId = realEstate.agentId
            )
        )
    }

    //TODO : function mapping
}