package com.openclassrooms.realestatemanager.ui.create

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.Repository
import com.openclassrooms.realestatemanager.data.model.Amenity
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.domain.RealEstateAgent
import com.openclassrooms.realestatemanager.domain.RealEstateToCreate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CreateViewModel(private val repository: Repository) : ViewModel() {


    //TODO : create object realEstate à envoyer en bdd

    //to get a list of agent's name
    fun getAgents(): LiveData<List<RealEstateAgent>> {
        return repository.getAllAgents().asLiveData()
    }

    private val _createdRealEstateMutableStateFlow = MutableStateFlow(RealEstateCreatedState())

    //state of data for UI
    val state: LiveData<RealEstateCreatedState> = _createdRealEstateMutableStateFlow.asLiveData()

    fun createRealEstate() {
        //TODO : get data to send to repo + factoring
        val currentState = _createdRealEstateMutableStateFlow.value

        val realEstateToCreate: RealEstateToCreate = RealEstateToCreate(
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
            agentId = currentState.agent!!.id
        )
        viewModelScope.launch {
            repository.createRealEstate(realEstateToCreate)
        }


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
}

/**
 * State to store data & enabled creation button
 */
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
    val agent: RealEstateAgent? = null
) {
    fun isCreatedEnabled(): Boolean {
        if (agent == null) {
            return false
        }
        return listOf(
            type?.toString(),
            address,
            city,
            price,
            surface,
            rooms,
            bedrooms,
            bathrooms,
            description,
        ).none { it.isNullOrBlank() }
    }
}

