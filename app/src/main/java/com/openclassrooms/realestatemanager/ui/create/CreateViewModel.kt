package com.openclassrooms.realestatemanager.ui.create

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
import com.openclassrooms.realestatemanager.utils.events.CreationSucceedEvent
import com.openclassrooms.realestatemanager.utils.events.InternetEvent
import com.openclassrooms.realestatemanager.utils.internetConnectivity.ConnectivityChecker
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.util.UUID

class CreateViewModel(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle,
    private val connectivityChecker: ConnectivityChecker
) : ViewModel() {


    //to get a list of agent's name
    fun getAgents(): LiveData<List<RealEstateAgent>> {
        return repository.getAllAgents().asLiveData()
    }

    //state of data stored in Bundle
    private val _createdRealEstateMutableStateFlow =
        savedStateHandle.getMutableStateFlow("KEY_STATE", RealEstateCreatedState())

    //state of data for UI
    val state: LiveData<RealEstateCreatedState> = _createdRealEstateMutableStateFlow.asLiveData()

    //Flow to be observe in UI, notifying internet connexion or not
    private val _internetEventFlow = Channel<InternetEvent>()
    val internetEventFlow = _internetEventFlow.receiveAsFlow()

    //Flow to be observe in UI, notifying creation success of real estate
    private val _isCreatedFlow = Channel<CreationSucceedEvent>()
    val isCreatedFlow = _isCreatedFlow.receiveAsFlow()


    fun addressInvalid() {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(isAddressValid = false)
        Log.i("createVM", "isAddressValid : ${_createdRealEstateMutableStateFlow.value}")
    }

    fun addressValid() {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(isAddressValid = true)
        Log.i("createVM", "isAddressValid : ${_createdRealEstateMutableStateFlow.value}")
    }

    /**
     * Create a realEstate from info enter by user
     */
    fun createRealEstate() {
        Log.i("createVM", "ça doit dismiss ")
        _isCreatedFlow.trySend(CreationSucceedEvent.isCreated)
        val currentState = _createdRealEstateMutableStateFlow.value

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

    }

    fun isPositionExist(address: String): Boolean {
        if (connectivityChecker.isInternetAvailable()) {
            return repository.isPositionExist(address)
        }
        return false
    }

    fun updateType(buildingType: BuildingType) {
        // ?: si null faire ce qui est a droite
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(type = buildingType)
        Log.i("createVM", "typeBuilding : ${_createdRealEstateMutableStateFlow.value}")
    }

    fun updateAddress(address: String) {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(address = address)
        Log.i("createVM", "address :${_createdRealEstateMutableStateFlow.value}")
    }

    fun updateCity(city: String) {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(city = city)
        Log.i("createVM", "city :${_createdRealEstateMutableStateFlow.value}")
    }

    fun updatePrice(price: String) {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(price = price)
        Log.i("createVM", "price :${_createdRealEstateMutableStateFlow.value}")
    }

    fun updateSurface(surface: String) {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(surface = surface)
        Log.i("createVM", "surface :${_createdRealEstateMutableStateFlow.value}")
    }

    fun updateRooms(rooms: String) {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(rooms = rooms)
        Log.i("createVM", "rooms : ${_createdRealEstateMutableStateFlow.value}")
    }

    fun updateBedrooms(bedrooms: String) {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(bedrooms = bedrooms)
        Log.i("createVM", "bedrooms :${_createdRealEstateMutableStateFlow.value}")
    }

    fun updateBathrooms(bathrooms: String) {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(bathrooms = bathrooms)
        Log.i("createVM", "bathrooms :${_createdRealEstateMutableStateFlow.value}")
    }

    fun updateDescription(description: String) {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(description = description)
        Log.i("createVM", "description :${_createdRealEstateMutableStateFlow.value}")
    }


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

    fun updateAgentName(agent: RealEstateAgent) {
        val currentState = _createdRealEstateMutableStateFlow.value
        _createdRealEstateMutableStateFlow.value = currentState.copy(agent = agent)
        Log.i("createVM", "agent :${_createdRealEstateMutableStateFlow.value}")
    }

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
    val isAddressValid: Boolean? = null
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
                && isAddressValid == true
    }
}



