package com.openclassrooms.realestatemanager.ui.create_edit

import android.net.Uri
import android.util.Log

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.Repository
import com.openclassrooms.realestatemanager.data.SaveResult
import com.openclassrooms.realestatemanager.data.model.Amenity
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.domain.Photo
import com.openclassrooms.realestatemanager.domain.PhotoChanges
import com.openclassrooms.realestatemanager.domain.RealEstateAgent
import com.openclassrooms.realestatemanager.domain.RealEstateToCreate
import com.openclassrooms.realestatemanager.domain.RealEstateToUpdate
import com.openclassrooms.realestatemanager.utils.PhotoSelectedViewState
import com.openclassrooms.realestatemanager.utils.events.CreationEvents
import com.openclassrooms.realestatemanager.utils.internetConnectivity.ConnectivityChecker
import com.openclassrooms.realestatemanager.utils.toPhotoSelectedViewState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class CreateEditViewModel(
    private val realEstateId: String?,
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
    private val connectivityChecker: ConnectivityChecker
) : ViewModel() {

    companion object {
        val KEY_STATE = "KEY_STATE"
        val KEY_PHOTO_CHANGES = "KEY_PHOTO_CHANGES"
    }

    //Boolean to know if it's a creation or an update
    private val _isCreation: Boolean = realEstateId == null
    val isCreation = _isCreation

    init {
        if (realEstateId != null) {
            //if there is nothing in savedStateHandle means it's the first time that the VM is created
            if (!savedStateHandle.contains(KEY_STATE)) {
                viewModelScope.launch {
                    val realEstate = repository.fetchOneRealEstate(realEstateId)
                    val agent = repository.fetchOneAgent(realEstate.agentId)
                    val realEstateToDisplay = RealEstateToSaveState(
                        type = realEstate.type,
                        address = realEstate.address,
                        city = realEstate.city,
                        price = realEstate.priceTag.toString(),
                        surface = realEstate.surface.toString(),
                        rooms = realEstate.rooms.toString(),
                        bedrooms = realEstate.rooms.toString(),
                        bathrooms = realEstate.bathrooms.toString(),
                        description = realEstate.description,
                        amenities = realEstate.amenities,
                        agent = agent,
                        status = realEstate.status,
                        dateOfSale = realEstate.dateOfSale,
                        dateOfCreation = realEstate.dateCreated,
                        photos = realEstate.photos.map { photo ->
                            photo.toPhotoSelectedViewState()
                        },
                    )
                    savedStateHandle[KEY_STATE] = realEstateToDisplay
                }

            }
        }
    }


    //to get a list of agent's name
    suspend fun fetchAgents(): List<RealEstateAgent> {
        return repository.fetchAllAgents()
    }

    //state of data stored in Bundle
    private val _savedRealEstateMutableStateFlow =
        savedStateHandle.getMutableStateFlow(KEY_STATE, RealEstateToSaveState())

    //state of photos modification stored in Bundle
    private val _photosChangedFlow =
        savedStateHandle.getMutableStateFlow(KEY_PHOTO_CHANGES, PhotoChanges())

    //state of data for UI
    val state: StateFlow<RealEstateToSaveState> =
        _savedRealEstateMutableStateFlow

    //Flow to be observe in UI, notifying creation success of real estate
    private val _isCreatedFlow = Channel<CreationEvents>()
    val isCreatedFlow = _isCreatedFlow.receiveAsFlow()

    /**
     * Create a realEstate from info enter by user
     */
    fun saveRealEstate() {
        val currentState = _savedRealEstateMutableStateFlow.value
        val currentPhotosChanges = _photosChangedFlow.value
        if (_isCreation) {

            val realEstateToCreate = RealEstateToCreate(
                type = currentState.type!!,
                photos = currentPhotosChanges.created,
                address = currentState.address!!.trim(),
                city = currentState.city!!.trim(),
                price = currentState.price!!.toFloat(),
                surface = currentState.surface!!.toInt(),
                rooms = currentState.rooms!!.toInt(),
                bedrooms = currentState.bedrooms!!.toInt(),
                bathrooms = currentState.bathrooms!!.toInt(),
                description = currentState.description!!,
                amenities = currentState.amenities,
                agentId = currentState.agent!!.id
            )

            viewModelScope.launch {
                val result = repository.createRealEstate(realEstateToCreate)
                when (result) {
                    SaveResult.SUCCESS -> _isCreatedFlow.trySend(CreationEvents.isCreated)
                    SaveResult.ERROR -> _isCreatedFlow.trySend(CreationEvents.failure)
                }
            }

        } else {

            if (realEstateId != null) {
                val realEstateToUpdate = RealEstateToUpdate(
                    id = this.realEstateId.toLong(),
                    type = currentState.type!!,
                    address = currentState.address!!.trim(),
                    city = currentState.city!!.trim(),
                    price = currentState.price!!.toFloat(),
                    surface = currentState.surface!!.toInt(),
                    rooms = currentState.rooms!!.toInt(),
                    bedrooms = currentState.bedrooms!!.toInt(),
                    bathrooms = currentState.bathrooms!!.toInt(),
                    description = currentState.description!!,
                    amenities = currentState.amenities,
                    status = currentState.status,
                    dateOfSale = currentState.dateOfSale,
                    dateOfCreation = currentState.dateOfCreation!!,
                    agentId = currentState.agent!!.id,
                    photoChanges = currentPhotosChanges
                )

                viewModelScope.launch {
                    val result =
                        repository.updateRealEstate(realEstateToUpdate)
                    when (result) {
                        SaveResult.SUCCESS -> _isCreatedFlow.trySend(CreationEvents.isUpdated)
                        SaveResult.ERROR -> _isCreatedFlow.trySend(CreationEvents.failure)
                    }

                }
            }


        }


    }


    /**
     * To update the type of real estate
     */
    fun updateType(buildingType: BuildingType) {
        val currentState = _savedRealEstateMutableStateFlow.value
        _savedRealEstateMutableStateFlow.value = currentState.copy(type = buildingType)
        Log.i("createVM", "typeBuilding : ${_savedRealEstateMutableStateFlow.value}")
    }

    /**
     * To update the real estate's address
     */
    fun updateAddress(address: String) {
        val currentState = _savedRealEstateMutableStateFlow.value
        _savedRealEstateMutableStateFlow.value = currentState.copy(address = address)
        Log.i("createVM", "address :${_savedRealEstateMutableStateFlow.value}")
    }

    /**
     * To update the city
     */
    fun updateCity(city: String) {
        val currentState = _savedRealEstateMutableStateFlow.value
        _savedRealEstateMutableStateFlow.value = currentState.copy(city = city)
        Log.i("createVM", "city :${_savedRealEstateMutableStateFlow.value}")
    }

    /**
     * To update surface in m2
     */
    fun updatePrice(price: String) {
        val currentState = _savedRealEstateMutableStateFlow.value
        _savedRealEstateMutableStateFlow.value = currentState.copy(price = price)
        Log.i("createVM", "price :${_savedRealEstateMutableStateFlow.value}")
    }

    /**
     * To update surface
     */
    fun updateSurface(surface: String) {
        val currentState = _savedRealEstateMutableStateFlow.value
        _savedRealEstateMutableStateFlow.value = currentState.copy(surface = surface)
        Log.i("createVM", "surface :${_savedRealEstateMutableStateFlow.value}")
    }

    /**
     * To update number of rooms
     */
    fun updateRooms(rooms: String) {
        val currentState = _savedRealEstateMutableStateFlow.value
        _savedRealEstateMutableStateFlow.value = currentState.copy(rooms = rooms)
        Log.i("createVM", "rooms : ${_savedRealEstateMutableStateFlow.value}")
    }

    /**
     * To update number of bedrooms
     */
    fun updateBedrooms(bedrooms: String) {
        val currentState = _savedRealEstateMutableStateFlow.value
        _savedRealEstateMutableStateFlow.value = currentState.copy(bedrooms = bedrooms)
        Log.i("createVM", "bedrooms :${_savedRealEstateMutableStateFlow.value}")
    }

    /**
     * To update number of bathroom
     */
    fun updateBathrooms(bathrooms: String) {
        val currentState = _savedRealEstateMutableStateFlow.value
        _savedRealEstateMutableStateFlow.value = currentState.copy(bathrooms = bathrooms)
        Log.i("createVM", "bathrooms :${_savedRealEstateMutableStateFlow.value}")
    }

    /**
     * To update real estate's description
     */
    fun updateDescription(description: String) {
        val currentState = _savedRealEstateMutableStateFlow.value
        _savedRealEstateMutableStateFlow.value =
            currentState.copy(description = description)
        Log.i("createVM", "description :${_savedRealEstateMutableStateFlow.value}")
    }

    /**
     * To update amenities
     */
    fun updateAmenities(amenity: Amenity, isChosen: Boolean) {
        val currentState = _savedRealEstateMutableStateFlow.value
        val listAmenities: MutableList<Amenity> = currentState.amenities.toMutableList()
        if (isChosen) {
            listAmenities.add(amenity)
        } else {
            listAmenities.remove(amenity)
        }
        //update the state
        _savedRealEstateMutableStateFlow.value =
            currentState.copy(amenities = listAmenities)
        Log.i("createVM", "amenities :${_savedRealEstateMutableStateFlow.value}")
    }


    /**
     * To update status
     */
    fun updateStatus(status: Status) {
        val currentState = _savedRealEstateMutableStateFlow.value
        _savedRealEstateMutableStateFlow.value = currentState.copy(status = status)
        Log.i("createVM", "status :${_savedRealEstateMutableStateFlow.value}")
    }

    /**
     * To update agent's name
     */
    fun updateAgentName(agent: RealEstateAgent) {
        val currentState = _savedRealEstateMutableStateFlow.value
        _savedRealEstateMutableStateFlow.value = currentState.copy(agent = agent)
        Log.i("createVM", "agent :${_savedRealEstateMutableStateFlow.value}")
    }

    /**
     * To add or update a photo from device
     */
    fun addPhotoPicker(uris: List<Uri>) {
        val currentState = _savedRealEstateMutableStateFlow.value
        //check if photo selected is in the list, if not add it into the list
        val selectedPhotos: List<PhotoSelectedViewState> = uris
            .filter { uri ->
                //map currentState.photos from List<Uri> to List<String>
                val existingUris = currentState.photos.map { photo ->
                    photo.uri
                }
                !existingUris.contains(uri.toString())

            }
            .map { uri ->
                PhotoSelectedViewState(
                    id = UUID.randomUUID().toString(),
                    uri = uri.toString(),
                    label = ""
                )

            }
        //add photo created to the photoCreated list
        val photos = selectedPhotos.map { photoSelectedViewState ->
            photoSelectedViewState.toPhoto()
        }
        _photosChangedFlow.update { state ->
            state.copy(created = state.created + photos)
        }

        _savedRealEstateMutableStateFlow.value =
            currentState.copy(photos = selectedPhotos + currentState.photos)
        Log.i("createVM", "photos :${_savedRealEstateMutableStateFlow.value}")
    }

    /**
     * To delete a photo
     */
    fun deletePhoto(id: String) {

        val currentState = _savedRealEstateMutableStateFlow.value
        val photos: List<PhotoSelectedViewState> = currentState.photos
            .filter { photo ->
                photo.id != id
            }

        _savedRealEstateMutableStateFlow.value = currentState.copy(photos = photos)

        //add photo's id deleted in photoDeleted list
        _photosChangedFlow.update { state ->
            state.copy(deleted = state.deleted + id)
        }
        Log.i("createVM", "${photos}")
        Log.i(
            "createVM",
            "photos without deleted one :${_savedRealEstateMutableStateFlow.value}"
        )
    }

    /**
     * To update the photo's label
     */
    fun updateLabel(label: String, id: String) {
        val currentState = _savedRealEstateMutableStateFlow.value
        val photosState: List<PhotoSelectedViewState> = currentState.photos.map { photo ->
            if (photo.id == id) {
                Log.i("VM", label)
                photo.copy(label = label)
            } else {
                photo
            }

        }
        val photoUpdated = photosState.first { photo ->
            photo.id == id
        }
        _photosChangedFlow.update { state ->
            state.copy(updated = state.updated + photoUpdated.toPhoto())
        }

        _savedRealEstateMutableStateFlow.value =
            currentState.copy(photos = photosState)
        Log.i("createVM", "labelUpdated :${_savedRealEstateMutableStateFlow.value}")
    }

    /**
     * To add a picture taken by camera device
     */
    fun addPhotoTaken(photoUri: String) {
        val currentState = _savedRealEstateMutableStateFlow.value
        val photoTaken = PhotoSelectedViewState(
            id = UUID.randomUUID().toString(),
            uri = photoUri,
            label = ""
        )
        val photos = currentState.photos.toMutableList()
        photos.add(0, photoTaken)

        //add photo created by using camera in photoCreated list
        _photosChangedFlow.update { state ->
            state.copy(created = state.created + photoTaken.toPhoto())
        }
        Log.i("VM", "$photoTaken")

        _savedRealEstateMutableStateFlow.value = currentState.copy(photos = photos)
        Log.i("createVM", "photo taken :${_savedRealEstateMutableStateFlow.value}")
    }

    /**
     * Update date of sale
     */
    fun updateDateOfSale(date: Instant?) {
        val currentState = _savedRealEstateMutableStateFlow.value
        _savedRealEstateMutableStateFlow.value = currentState.copy(dateOfSale = date)
    }

    // MAPPING FUNCTION HERE
    fun PhotoSelectedViewState.toPhoto(): Photo {
        return Photo(
            id = this.id,
            urlPhoto = this.uri,
            label = this.label
        )
    }
}



