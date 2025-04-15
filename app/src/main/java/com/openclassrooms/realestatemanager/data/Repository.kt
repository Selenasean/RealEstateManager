package com.openclassrooms.realestatemanager.data

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

class Repository(
    private val appdatabase: AppDataBase,
    private val geocoderRepository: GeocoderRepository
) {


    /**
     * Get all RealEstates
     */
    fun getAllRealEstates(): Flow<List<RealEstate>> {

        return appdatabase.realEstateDao().getAllRealEstates().map { realEstateDbs ->
            realEstateDbs.map {
                RealEstate(
                    id = it.key.id.toString(),
                    title = it.key.name,
                    city = it.key.city,
                    priceTag = it.key.price,
                    type = it.key.type,
                    photos = it.value.map { photoDb ->
                        Photo(
                            photoDb.id,
                            photoDb.urlPhoto,
                            photoDb.label
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
                    latitude = it.key.latitude,
                    longitude = it.key.longitude
                )
            }
        }
    }

    /**
     * Get One RealEstates
     */
    fun getOneRealEstates(realEstateId: String): Flow<RealEstate> {
        return appdatabase.realEstateDao().getOneRealEstate(realEstateId.toLong())
            .map { realEstateDb ->
                val entry = realEstateDb.entries.first()
                val photos: List<PhotoDb> = entry.value
                RealEstate(
                    id = entry.key.id.toString(),
                    title = entry.key.name,
                    city = entry.key.city,
                    priceTag = entry.key.price,
                    type = entry.key.type,
                    photos = photos.map { photoDb ->
                        Photo(
                            id = photoDb.id,
                            urlPhoto = photoDb.urlPhoto,
                            label = photoDb.label
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
                    latitude = entry.key.latitude,
                    longitude = entry.key.longitude
                )

            }

    }

    /**
     * To get all Agents
     */
    fun getAllAgents(): Flow<List<RealEstateAgent>> {
        return appdatabase.realEstateAgentDao().getAllAgents().map { realEstateAgentDb ->
            realEstateAgentDb.map {
                RealEstateAgent(
                    id = it.id,
                    name = it.name
                )
            }
        }

    }

    /**
     * Create RealEstate in database + photos associated
     */
    suspend fun createRealEstate(realEstate: RealEstateToCreate) {
        val position = geocoderRepository.getLongLat(realEstate.address)
        if (position != null) {
            val realEstateCreatedId = appdatabase.realEstateDao().createRealEstate(
                RealEstateDb(
                    type = realEstate.type,
                    price = realEstate.price,
                    name = "",
                    surface = realEstate.surface,
                    rooms = realEstate.rooms,
                    bedrooms = realEstate.bedrooms,
                    bathrooms = realEstate.bathrooms,
                    description = realEstate.description,
                    address = realEstate.address,
                    city = realEstate.city,
                    status = Status.FOR_SALE,
                    amenities = realEstate.amenities,
                    dateCreated = Instant.now(),
                    dateOfSale = null,
                    realEstateAgentId = realEstate.agentId,
                    latitude = position.latitude,
                    longitude = position.longitude
                )
            )

            realEstate.photos.forEach { photo ->
                appdatabase.photoDao().createPhoto(
                    PhotoDb(
                        id = photo.id,
                        realEstateId = realEstateCreatedId.toString(),
                        urlPhoto = photo.urlPhoto,
                        label = photo.label
                    )
                )

            }
            return

        }
    }

    /**
     * To know if localisation actualy exist
     */
    fun isPositionExist(address: String): Boolean {
        val position = geocoderRepository.getLongLat(address)
        return position != null
    }

    //TODO : function mapping here ?
}