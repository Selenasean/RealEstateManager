package com.openclassrooms.realestatemanager.data

import android.util.Log
import com.openclassrooms.realestatemanager.data.model.PhotoDb
import com.openclassrooms.realestatemanager.data.model.RealEstateAgentDb
import com.openclassrooms.realestatemanager.data.model.RealEstateDb
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.domain.Photo
import com.openclassrooms.realestatemanager.domain.RealEstate
import com.openclassrooms.realestatemanager.domain.RealEstateAgent
import com.openclassrooms.realestatemanager.domain.RealEstateToCreate
import com.openclassrooms.realestatemanager.domain.RealEstateToUpdate
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
                    longitude = it.key.longitude,
                    agentId = it.key.realEstateAgentId,
                    dateCreated = it.key.dateCreated,
                    dateOfSale = it.key.dateOfSale
                )
            }
        }
    }

    /**
     * Get One RealEstate
     * type Flow<RealEstate>
     */
    fun getOneRealEstates(realEstateId: String): Flow<RealEstate> {
        return appdatabase.realEstateDao().getOneRealEstate(realEstateId.toLong())
            .map { realEstateDb ->
                realEstateDb.toRealEstate()
            }

    }


    /**
     * To get one particular real estate
     * type RealEstate
     */
    suspend fun fetchOneRealEstate(realEstateId: String): RealEstate {
        return appdatabase.realEstateDao().fetchOneRealEstate(realEstateId.toLong()).toRealEstate()
    }

    /**
     * To get all Agents
     */
    fun getAllAgents(): Flow<List<RealEstateAgent>> {
        return appdatabase.realEstateAgentDao().getAllAgents().map { realEstateAgentDb ->
            realEstateAgentDb.map {
                it.toRealEstateAgent()
            }
        }
    }

    /**
     * To get all agents
     */
    suspend fun fetchAllAgents(): List<RealEstateAgent> {
        return appdatabase.realEstateAgentDao().fetchAllAgents().map { agent ->
            agent.toRealEstateAgent()
        }
    }

    /**
     * To get one specific agent
     */
    suspend fun fetchOneAgent(agentId: Long): RealEstateAgent {
        return appdatabase.realEstateAgentDao().fetchOneAgent(agentId).toRealEstateAgent()
    }

    /**
     * Create RealEstate in database + photos associated
     */
    suspend fun createRealEstate(realEstate: RealEstateToCreate): SaveResult {
        val position = geocoderRepository.getLongLat(realEstate.address)
        if (position == null) return SaveResult.ERROR

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
        return SaveResult.SUCCESS

    }

    /**
     * To update a realEstate
     */
    suspend fun updateRealEstate(realEstate: RealEstateToUpdate, oldRealEstateId: Long): SaveResult {
        val position = geocoderRepository.getLongLat(realEstate.address)
        if(position == null) return SaveResult.ERROR
        appdatabase.realEstateDao().updateRealEstate(
            RealEstateDb(
                id = oldRealEstateId,
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
                status = realEstate.status,
                amenities = realEstate.amenities,
                dateCreated = Instant.now(),
                dateOfSale = realEstate.dateOfSale,
                realEstateAgentId = realEstate.agentId,
                latitude = position.latitude,
                longitude = position.longitude
            )
        )

        realEstate.photoChanges.deleted.forEach { photoId ->
            appdatabase.photoDao().deleteFromId(photoId)
        }
        realEstate.photoChanges.updated.forEach { photo ->
            appdatabase.photoDao().updatePhoto(photo.toPhotoDb(oldRealEstateId))
            Log.i("repo update photo", "updateRealEstate: photo updated ")
        }
        realEstate.photoChanges.created.forEach { photo ->
            appdatabase.photoDao().createPhoto(photo.toPhotoDb(oldRealEstateId))
            Log.i("repo create photo", "create photo")
        }

        return SaveResult.SUCCESS
    }

    /**
     * To know if localisation actually exist
     */
    fun isPositionExist(address: String): Boolean {
        val position = geocoderRepository.getLongLat(address)
        return position != null
    }

    // MAPPING FUNCTION HERE
    fun Map<RealEstateDb, List<PhotoDb>>.toRealEstate(): RealEstate {
        val entry = this.entries.first()
        val photos: List<PhotoDb> = entry.value
        return RealEstate(
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
            longitude = entry.key.longitude,
            agentId = entry.key.realEstateAgentId,
            dateCreated = entry.key.dateCreated,
            dateOfSale = entry.key.dateOfSale
        )

    }

    fun RealEstateAgentDb.toRealEstateAgent(): RealEstateAgent {
        return RealEstateAgent(
            id = this.id,
            name = this.name
        )
    }

    fun Photo.toPhotoDb(realEstateId: Long): PhotoDb {
        return  PhotoDb(
            id = this.id,
            realEstateId = realEstateId.toString(),
            urlPhoto = this.urlPhoto,
            label = this.label
        )
    }
}

enum class SaveResult() {
    SUCCESS,
    ERROR
}