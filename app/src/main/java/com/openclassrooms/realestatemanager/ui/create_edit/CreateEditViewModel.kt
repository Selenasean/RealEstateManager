package com.openclassrooms.realestatemanager.ui.create_edit

import android.net.Uri
import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.Repository
import com.openclassrooms.realestatemanager.data.model.Amenity
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.domain.Photo
import com.openclassrooms.realestatemanager.domain.RealEstateAgent
import com.openclassrooms.realestatemanager.domain.RealEstateToCreate
import com.openclassrooms.realestatemanager.utils.PhotoSelectedViewState
import com.openclassrooms.realestatemanager.utils.events.CreationEvents
import com.openclassrooms.realestatemanager.utils.internetConnectivity.ConnectivityChecker
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.util.UUID

class CreateEditViewModel(
    realEstateId: String?,
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
    private val connectivityChecker: ConnectivityChecker
) : ViewModel() {


    init {
        if (realEstateId == null) {
            //on create fragment : state vide
            Log.i("init createVM", "null")
        } else {
            //on update fragment : state deja existant
            Log.i("init createVM", realEstateId)
        }
    }

    //to get a list of agent's name
    fun getAgents(): LiveData<List<RealEstateAgent>> {
        return repository.getAllAgents().asLiveData()
    }

    //state of data stored in Bundle
    private val _createdRealEstateMutableStateFlow =
        savedStateHandle.getMutableStateFlow("KEY_STATE", RealEstateCreatedState())

    //state of data for UI
    val state: LiveData<RealEstateCreatedState> = _createdRealEstateMutableStateFlow.asLiveData()

    //Flow to be observe in UI, notifying creation success of real estate
    private val _isCreatedFlow = Channel<CreationEvents>()
    val isCreatedFlow = _isCreatedFlow.receiveAsFlow()

    /**
     * Create a realEstate from info enter by user
     */
    fun createRealEstate() {
        val currentState = _createdRealEstateMutableStateFlow.value

        if (isPositionExist()) {
            val realEstateToCreate = RealEstateToCreate(
                type = currentState.type!!,
                photos = currentState.photos.map { photo ->
                    Photo(
                        id = photo.id,
                        urlPhoto = photo.uri,
                        label = photo.label
                    )
                },
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
                repository.createRealEstate(realEstateToCreate)
            }

            _isCreatedFlow.trySend(CreationEvents.isCreated)
        }

    }

    /**
     * Boolean to know if address enter by user exist, if so means also there is an internet connectivity
     */
    fun isPositionExist(): Boolean {
        if (connectivityChecker.isInternetAvailable()) {
            return repository.isPositionExist(_createdRealEstateMutableStateFlow.value.address.toString())
        }
        return false
    }

    /**
     * To update the type of real estate
     */
    fun updateType(buildingType: BuildingType) {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(type = buildingType)
        Log.i("createVM", "typeBuilding : ${_createdRealEstateMutableStateFlow.value}")
    }

    /**
     * To update the real estate's address
     */
    fun updateAddress(address: String) {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(address = address)
        Log.i("createVM", "address :${_createdRealEstateMutableStateFlow.value}")
    }

    /**
     * To update the city
     */
    fun updateCity(city: String) {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(city = city)
        Log.i("createVM", "city :${_createdRealEstateMutableStateFlow.value}")
    }

    /**
     * To update surface in m2
     */
    fun updatePrice(price: String) {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(price = price)
        Log.i("createVM", "price :${_createdRealEstateMutableStateFlow.value}")
    }

    /**
     * To update surface
     */
    fun updateSurface(surface: String) {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(surface = surface)
        Log.i("createVM", "surface :${_createdRealEstateMutableStateFlow.value}")
    }

    /**
     * To update number of rooms
     */
    fun updateRooms(rooms: String) {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(rooms = rooms)
        Log.i("createVM", "rooms : ${_createdRealEstateMutableStateFlow.value}")
    }

    /**
     * To update number of bedrooms
     */
    fun updateBedrooms(bedrooms: String) {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(bedrooms = bedrooms)
        Log.i("createVM", "bedrooms :${_createdRealEstateMutableStateFlow.value}")
    }

    /**
     * To update number of bathroom
     */
    fun updateBathrooms(bathrooms: String) {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(bathrooms = bathrooms)
        Log.i("createVM", "bathrooms :${_createdRealEstateMutableStateFlow.value}")
    }

    /**
     * To update real estate's description
     */
    fun updateDescription(description: String) {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(description = description)
        Log.i("createVM", "description :${_createdRealEstateMutableStateFlow.value}")
    }

    /**
     * To update amenities
     */
    fun updateAmenities(amenity: Amenity, isChosen: Boolean) {
        val currentState = _createdRealEstateMutableStateFlow.value
        val listAmenities: MutableList<Amenity> = currentState.amenities.toMutableList()
        if (isChosen) {
            listAmenities.add(amenity)
        } else {
            listAmenities.remove(amenity)
        }
        //update the state
        _createdRealEstateMutableStateFlow.value = currentState.copy(amenities = listAmenities)
        Log.i("createVM", "amenities :${_createdRealEstateMutableStateFlow.value}")
    }

    /**
     * To update agent's name
     */
    fun updateAgentName(agent: RealEstateAgent) {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(agent = agent)
        Log.i("createVM", "agent :${_createdRealEstateMutableStateFlow.value}")
    }

    /**
     * To update a photo
     */
    fun updatePhotos(uris: List<Uri>) {
        val currentState = _createdRealEstateMutableStateFlow.value
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

        _createdRealEstateMutableStateFlow.value =
            currentState.copy(photos = selectedPhotos + currentState.photos)
        Log.i("createVM", "photos :${_createdRealEstateMutableStateFlow.value}")
    }

    /**
     * To delete a photo
     */
    fun deletePhoto(id: String) {

        val currentState = _createdRealEstateMutableStateFlow.value
        val photos: List<PhotoSelectedViewState> = currentState.photos
            .filter { photo ->
                photo.id != id
            }

        _createdRealEstateMutableStateFlow.value = currentState.copy(photos = photos)
        Log.i("createVM", "${photos}")
        Log.i("createVM", "photos without deleted one :${_createdRealEstateMutableStateFlow.value}")
    }

    /**
     * To update the photo's label
     */
    fun updateLabel(label: String, id: String) {
        val currentState = _createdRealEstateMutableStateFlow.value
        val photosUpdated: List<PhotoSelectedViewState> = currentState.photos.map { photo ->
            if (photo.id == id) {
                photo.copy(label = label)
            } else {
                photo
            }
        }
        _createdRealEstateMutableStateFlow.value =
            currentState.copy(photos = photosUpdated)
        Log.i("createVM", "labelUpdated :${_createdRealEstateMutableStateFlow.value}")
    }

    /**
     * To add a picture taken by device
     */
    fun addPictureTaken(pictureTaken: String) {
        val currentState = _createdRealEstateMutableStateFlow.value
        val photoTaken = PhotoSelectedViewState(
            id = UUID.randomUUID().toString(),
            uri = pictureTaken,
            label = ""
        )
        val photos = currentState.photos.toMutableList()
        photos.add(0, photoTaken)
        _createdRealEstateMutableStateFlow.value = currentState.copy(photos = photos)
        Log.i("createVM", "photo taken :${_createdRealEstateMutableStateFlow.value}")
    }
}


/**
 * State to store data & enabled creation button
 */
@Parcelize
data class RealEstateCreatedState(
    val type: BuildingType? = null,
    val address: String? = null,
    val city: String? = null,
    val price: String? = null,
    val surface: String? = null,
    val rooms: String? = null,
    val bedrooms: String? = null,
    val bathrooms: String? = null,
    val description: String? = null,
    val amenities: List<Amenity> = emptyList(),
    val agent: RealEstateAgent? = null,
    val photos: List<PhotoSelectedViewState> = emptyList(),
) : Parcelable {

    fun isCreatedEnabled(): Boolean {
        return listOf(
            type.toString(),
            address,
            city,
            price,
            surface,
            rooms,
            bedrooms,
            bathrooms,
            description,
        ).none { it.isNullOrBlank() }
                && agent != null
                && photos.isNotEmpty()
    }
}



