package com.openclassrooms.realestatemanager.ui.list_map_details


import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.FilterRepository
import com.openclassrooms.realestatemanager.data.Position
import com.openclassrooms.realestatemanager.data.Repository
import com.openclassrooms.realestatemanager.data.location.LocationRepository
import com.openclassrooms.realestatemanager.data.model.Amenity
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.domain.Photo
import com.openclassrooms.realestatemanager.utils.PhotoSelectedViewState
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.events.ListMapDetailEvent
import com.openclassrooms.realestatemanager.utils.events.MapEvent
import com.openclassrooms.realestatemanager.utils.toPhotoSelectedViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class ListMapDetailViewModel(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle,
    private val locationRepository: LocationRepository,
    private val filtersRepository: FilterRepository
) : ViewModel() {

    companion object{
        val ID_KEY = "ID_KEY"
    }

    //state of Pane : open or close, depending if there is id or not, to display the right fragment
    private var _detailPaneIdStateFlow: MutableStateFlow<String?> =
        savedStateHandle.getMutableStateFlow(ID_KEY, null)

    //state of realEstate position open in detail
    private var _positionStateFlow = MutableStateFlow<Position?>(null)
    val positionStateFlow = _positionStateFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    //Flows to be observe in UI, notifying an event
    private val _eventsFlow = Channel<ListMapDetailEvent>()
    val eventsFlow = _eventsFlow.receiveAsFlow()

    private val _eventMapFlow = Channel<MapEvent>()
    val eventMapFlow = _eventMapFlow.receiveAsFlow()

    /**
     * To get data from one realEstate to be observed in UI
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val detailState: StateFlow<RealEstateDetailViewState?> = _detailPaneIdStateFlow
        .flatMapLatest { id ->
            if (id != null && id.isNotEmpty()) {

                repository.getOneRealEstate(id).map { realEstate ->

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
                        longitude = realEstate.longitude,
                        dateOfSale = realEstate.dateOfSale?.let { Utils.instantToDate(it) }
                    )

                }

            } else {
                flowOf(null)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )


    /**
     * Stock id value for opening the right DetailFragment
     */
    fun onRealEstateClick(id: String) {
        println("click realestate =$id")
        _detailPaneIdStateFlow.value = id
        _eventsFlow.trySend(ListMapDetailEvent.OpenDetails)
    }

    /**
     * Reset value, if its null means the DetailFragment is closed
     */
    fun onDetailClosed() {
        _detailPaneIdStateFlow.value = null
    }

    /**
     * To get list of ItemState to observe in UI
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val listRealEstates = filtersRepository.getFilters().flatMapLatest { filter ->
        repository.getAllRealEstates(filter)
    }

    val listState: StateFlow<List<ItemState>> =
        listRealEstates
            .combine(_detailPaneIdStateFlow) { listRealEstate, idSelected ->
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
                            amenities = realEstate.amenities,
                            dateOfSale = realEstate.dateOfSale?.let { Utils.instantToDate(it) }
                        )
                    )
                }
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )


    /**
     * To get list of realEstate with Positions
     */
    val mapList: StateFlow<List<MapState>> =
        listRealEstates
            .combine(_detailPaneIdStateFlow) { realEstateList, idSelected ->
                realEstateList.mapNotNull { realEstate ->

                    if (true) {
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
                    } else {
                        null
                    }

                }
            }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    /**
     * To get the position of a RealEstate and store it in stateFlow
     */
    fun realEstatePosition(position: Position) {
        _positionStateFlow.value = position
    }

    /**
     * To check if location exist and send an Event to get the location in UI
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun onLocationPermission() {
        viewModelScope.launch {
            val location = locationRepository.getLastLocation()
            if (location != null) {
                _eventMapFlow.trySend(MapEvent.CenterUserLocation(location))
            }
        }

    }

}

// DATA CLASSES FOR STATE
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
    val amenities: List<Amenity>,
    val dateOfSale: String?
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
    val longitude: Double?,
    val dateOfSale: String?
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
    val longitude: Double,
    val latitude: Double
)


