package com.openclassrooms.realestatemanager.data


import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import android.database.Cursor
import com.openclassrooms.realestatemanager.data.dao.PhotoDao
import com.openclassrooms.realestatemanager.data.dao.RealEstateAgentDao
import com.openclassrooms.realestatemanager.data.dao.RealEstateDao
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.PhotoDb

import com.openclassrooms.realestatemanager.data.model.RealEstateDb
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.domain.Filters

import com.openclassrooms.realestatemanager.domain.RealEstate
import com.openclassrooms.realestatemanager.domain.RealEstateAgent

import com.openclassrooms.realestatemanager.domain.RealEstateToCreate
import com.openclassrooms.realestatemanager.domain.RealEstateToUpdate
import com.openclassrooms.realestatemanager.domain.toPhotoDb
import com.openclassrooms.realestatemanager.domain.toRealEstate
import com.openclassrooms.realestatemanager.domain.toRealEstateAgent

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock
import java.time.Instant


class Repository(
    private val photoDao: PhotoDao,
    private val realEstateDao: RealEstateDao,
    private val realEstateAgentDao: RealEstateAgentDao,
    private val geocoderRepository: GeocoderRepository
) {

    /**
     * Need primary constructor for production use,
     * the second constructor for testing
     */
    constructor(
        appdatabase: AppDataBase,
        geocoderRepository: GeocoderRepository
    ) : this(
        appdatabase.photoDao(),
        appdatabase.realEstateDao(),
        appdatabase.realEstateAgentDao(),
        geocoderRepository
    )

    private val clock: Clock = Clock.systemUTC()

    /**
     * Get all RealEstates
     */
    fun getAllRealEstates(filter: Filters): Flow<List<RealEstate>> {
        return this.realEstateDao.getAllRealEstates(
            city = filter.city,
            type = filter.type.ifEmpty { BuildingType.entries },
            minPrice = filter.priceMin,
            maxPrice = filter.priceMax,
            minSurface = filter.surfaceMin,
            maxSurface = filter.surfaceMax,
            status = filter.status
        ).map { realEstate ->
            realEstate.toRealEstate()
        }

    }

    /**
     * Get One RealEstate
     * type Flow<RealEstate>
     */
    fun getOneRealEstate(realEstateId: String): Flow<RealEstate> {
        return this.realEstateDao.getOneRealEstate(realEstateId.toLong())
            .map { realEstateDb ->
                realEstateDb.toRealEstate().first()
            }

    }


    /**
     * To get one particular real estate
     * type RealEstate
     */
    suspend fun fetchOneRealEstate(realEstateId: String): RealEstate {
        return this.realEstateDao.fetchOneRealEstate(realEstateId.toLong()).toRealEstate()
            .first()
    }


    /**
     * To get all agents
     * type List<RealEstateAgent>
     */
    suspend fun fetchAllAgents(): List<RealEstateAgent> {
        return this.realEstateAgentDao.fetchAllAgents().map { agent ->
            agent.toRealEstateAgent()
        }
    }

    /**
     * To get one specific agent
     * type RealEstateAgent
     */
    suspend fun fetchOneAgent(agentId: Long): RealEstateAgent {
        return this.realEstateAgentDao.fetchOneAgent(agentId).toRealEstateAgent()
    }

    /**
     * Create RealEstate in database + photos associated
     */
    suspend fun createRealEstate(realEstate: RealEstateToCreate): SaveResult {
        val position = geocoderRepository.getLongLat(realEstate.address)
        if (position == null) return SaveResult.ERROR

        val realEstateCreatedId = this.realEstateDao.createRealEstate(
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
                dateCreated = clock.instant(), //to get the current instant
                dateOfSale = null,
                realEstateAgentId = realEstate.agentId,
                latitude = position.latitude,
                longitude = position.longitude,
                video =  "android.resource://" + BuildConfig.APPLICATION_ID + "/" + R.raw.sample_video
            )
        )

        realEstate.photos.forEach { photo ->
            this.photoDao.createPhoto(
                PhotoDb(
                    id = photo.id,
                    realEstateId = realEstateCreatedId,
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
    suspend fun updateRealEstate(
        realEstate: RealEstateToUpdate,
    ): SaveResult {
        val position = geocoderRepository.getLongLat(realEstate.address)
        if (position == null) return SaveResult.ERROR
        this.realEstateDao.updateRealEstate(
            RealEstateDb(
                id = realEstate.id,
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
                dateCreated = realEstate.dateOfCreation,
                dateOfSale = realEstate.dateOfSale,
                realEstateAgentId = realEstate.agentId,
                latitude = position.latitude,
                longitude = position.longitude,
                video = "android.resource://" + BuildConfig.APPLICATION_ID + "/" + R.raw.sample_video
            )
        )

        realEstate.photoChanges.deleted.forEach { photoId ->
            this.photoDao.deleteFromId(photoId)
        }
        realEstate.photoChanges.updated.forEach { photo ->
            this.photoDao.updatePhoto(photo.toPhotoDb(realEstate.id))
        }
        realEstate.photoChanges.created.forEach { photo ->
            this.photoDao.createPhoto(photo.toPhotoDb(realEstate.id))

        }

        return SaveResult.SUCCESS
    }

    /**
     * For content provider
     * @return Cursor
     */
    fun getAllRealEstatesWithCursor(): Cursor{
        return realEstateDao.getAllRealEstatesWithCursor()
    }

    /**
     * For content provider
     * @param id of the realEstate wanted
     * @return Cursor of the realEstate
     */
    fun getOneRealEstateWithCursor(id: Long): Cursor{
        return realEstateDao.getOneRealEstateWithCursor(id)
    }

}


/**
 * Enum class for results of request
 */
enum class SaveResult() {
    SUCCESS,
    ERROR
}

