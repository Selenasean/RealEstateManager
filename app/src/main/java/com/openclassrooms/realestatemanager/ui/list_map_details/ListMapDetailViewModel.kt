package com.openclassrooms.realestatemanager.ui.list_map_details


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.data.GeocoderRepository
import com.openclassrooms.realestatemanager.data.Repository
import com.openclassrooms.realestatemanager.data.model.Amenity
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.domain.Photo
import com.openclassrooms.realestatemanager.utils.PhotoSelectedViewState
import com.openclassrooms.realestatemanager.utils.toPhotoSelectedViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow


class ListMapDetailViewModel(
    private val repository: Repository,
    private val geocoderRepository: GeocoderRepository
) : ViewModel() {

    //state of Pane : open or close, depending if there is id or not, to display the right fragment
    private var selectedStateFlow = MutableStateFlow<String?>(null)

    //Flow to be observe in UI, notifying of an Event
    private val _eventsFlow = Channel<Event>()
    val eventsFlow = _eventsFlow.receiveAsFlow()


    /**
     * To get data from one realEstate to be observed in UI
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val detailState: LiveData<RealEstateDetailViewState?> = selectedStateFlow
        .flatMapLatest { id ->
            if (id != null) {
                repository.getOneRealEstates(id).map { realEstate ->
                    RealEstateDetailViewState(
                        id = realEstate.id,
                        title = realEstate.title,
                        city = realEstate.city,
                        priceTag = realEstate.priceTag,
                        type = realEstate.type,
                        photos = realEstate.photos.map { photo ->
                            photo.toPhotoSelectedViewState()
                        },
                        surface = realEstate.surface,
                        rooms = realEstate.rooms,
                        bathrooms = realEstate.bathrooms,
                        bedrooms = realEstate.bedrooms,
                        description = realEstate.description,
                        address = realEstate.address,
                        status = realEstate.status,
                        amenities = realEstate.amenities,
                        latitude = realEstate.latitude,
                        longitude = realEstate.longitude
                    )

                }
            } else {
                flowOf(null)
            }
        }.asLiveData()

    /**
     * Stock id value for opening the right DetailFragment
     */
    fun onRealEstateClick(id: String) {
        selectedStateFlow.value = id
        _eventsFlow.trySend(Event.OpenDetails)
    }

    /**
     * Reset value, if its null means the DetailFragment is closed
     */
    fun onDetailClosed() {
        selectedStateFlow.value = null
    }

    /**
     * get list of ItemState to observe in UI
     */
    val listState: LiveData<List<ItemState>> =
        repository.getAllRealEstates().combine(selectedStateFlow) { listRealEstate, idSelected ->
            listRealEstate.map { realEstate ->
                ItemState(
                    isSelected = realEstate.id == idSelected,
                    realEstate = RealEstateViewState(
                        id = realEstate.id,
                        title = realEstate.title,
                        city = realEstate.city,
                        priceTag = realEstate.priceTag,
                        type = realEstate.type,
                        photos = realEstate.photos,
                        surface = realEstate.surface,
                        rooms = realEstate.rooms,
                        bathrooms = realEstate.bathrooms,
                        bedrooms = realEstate.bedrooms,
                        description = realEstate.description,
                        address = realEstate.address,
                        status = realEstate.status,
                        amenities = realEstate.amenities
                    )
                )
            }
        }.asLiveData()



    /**
     * To get list of realEstate with Positions
     */
    val mapList: LiveData<List<MapState>> =
        repository.getAllRealEstates().combine(selectedStateFlow) { realEstateList, idSelected ->
            realEstateList.map { realEstate ->
                MapState(
                    id = realEstate.id,
                    city = realEstate.city,
                    type = realEstate.type,
                    priceTag = realEstate.priceTag,
                    status = realEstate.status,
                    isSelected = realEstate.id == idSelected,
                    longitude = realEstate.longitude,
                    latitude = realEstate.latitude
                )
            }.filter {
                it.longitude != null
            }
        }.asLiveData()

}


/**
 * State for items on the recyclerView used on the list view
 */
data class ItemState(
    val isSelected: Boolean,
    val realEstate: RealEstateViewState
)

/**
 * State for real estate infos used in the ItemState
 */
data class RealEstateViewState(
    val id: String,
    val title: String,
    val city: String,
    val priceTag: Float,
    val type: BuildingType,
    val photos: List<Photo>,
    val surface: Int,
    val rooms: Int,
    val bathrooms: Int,
    val bedrooms: Int,
    val description: String,
    val address: String,
    val status: Status,
    val amenities: List<Amenity>
)

/**
 * State for the detail view
 */
data class RealEstateDetailViewState(
    val id: String,
    val title: String,
    val city: String,
    val priceTag: Float,
    val type: BuildingType,
    val photos: List<PhotoSelectedViewState>,
    val surface: Int,
    val rooms: Int,
    val bathrooms: Int,
    val bedrooms: Int,
    val description: String,
    val address: String,
    val status: Status,
    val amenities: List<Amenity>,
    val latitude: Double?,
    val longitude: Double?
)

/**
 * State for displayed realEstate on map
 */
data class MapState(
    val id: String,
    val type: BuildingType,
    val city: String,
    val priceTag: Float,
    val isSelected: Boolean,
    val status: Status,
    val longitude: Double?,
    val latitude: Double?
)

/**
 * To deal with events
 */
sealed interface Event {
    data object OpenDetails : Event
}