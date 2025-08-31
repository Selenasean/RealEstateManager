package com.openclassrooms.realestatemanager.data

import android.util.Log
import com.openclassrooms.realestatemanager.data.dao.PhotoDao
import com.openclassrooms.realestatemanager.data.dao.RealEstateAgentDao
import com.openclassrooms.realestatemanager.data.dao.RealEstateDao
import com.openclassrooms.realestatemanager.data.model.PhotoDb
import com.openclassrooms.realestatemanager.data.model.RealEstateAgentDb
import com.openclassrooms.realestatemanager.data.model.RealEstateDb
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.domain.Filters
import com.openclassrooms.realestatemanager.domain.Photo
import com.openclassrooms.realestatemanager.domain.RealEstate
import com.openclassrooms.realestatemanager.domain.RealEstateAgent
import com.openclassrooms.realestatemanager.domain.RealEstateToCreate
import com.openclassrooms.realestatemanager.domain.RealEstateToUpdate
import com.openclassrooms.realestatemanager.domain.toRealEstate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class Repository(
    private val photoDao: PhotoDao,
    private val realEstateDao: RealEstateDao,
    private val realEstateAgentDao: RealEstateAgentDao,
    private val geocoderRepository: GeocoderRepository
) {

    /**
     * Need primary construtor for production use,
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


    /**
     * Get all RealEstates
     */
    fun getAllRealEstates(filter: Filters?): Flow<List<RealEstate>> {
        if (filter == null) {
            return this.realEstateDao.getAllRealEstates().map { realEstateDbs ->
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
        } else {
            return this.realEstateDao.getRealEstatesFiltered(
                city = filter.city,
                type = filter.type,
                minPrice = filter.priceMin,
                maxPrice = filter.priceMax,
                minSurface = filter.surfaceMin,
                maxSurface = filter.surfaceMax,
                status = filter.status
            ).map { realEstates ->
                Log.i("getFilteredRealEst", "getFilteredRealEstates: $realEstates ")
                realEstates.toRealEstate()

            }


        }
    }

        /**
         * Get One RealEstate
         * type Flow<RealEstate>
         */
        fun getOneRealEstates(realEstateId: String): Flow<RealEstate> {
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
                    dateCreated = Instant.now(),
                    dateOfSale = null,
                    realEstateAgentId = realEstate.agentId,
                    latitude = position.latitude,
                    longitude = position.longitude
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
            oldRealEstateId: Long
        ): SaveResult {
            val position = geocoderRepository.getLongLat(realEstate.address)
            if (position == null) return SaveResult.ERROR
            this.realEstateDao.updateRealEstate(
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
                this.photoDao.deleteFromId(photoId)
            }
            realEstate.photoChanges.updated.forEach { photo ->
                this.photoDao.updatePhoto(photo.toPhotoDb(oldRealEstateId))
                Log.i("repo update photo", "updateRealEstate: photo updated ")
            }
            realEstate.photoChanges.created.forEach { photo ->
                this.photoDao.createPhoto(photo.toPhotoDb(oldRealEstateId))
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
        fun RealEstateAgentDb.toRealEstateAgent(): RealEstateAgent {
            return RealEstateAgent(
                id = this.id,
                name = this.name
            )
        }

        fun Photo.toPhotoDb(realEstateId: Long): PhotoDb {
            return PhotoDb(
                id = this.id,
                realEstateId = realEstateId,
                urlPhoto = this.urlPhoto,
                label = this.label
            )
        }
    }

    enum class SaveResult() {
        SUCCESS,
        ERROR
    }