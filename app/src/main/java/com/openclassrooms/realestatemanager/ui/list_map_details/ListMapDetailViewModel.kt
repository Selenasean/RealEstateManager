package com.openclassrooms.realestatemanager.ui.list_map_details


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
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


class ListMapDetailViewModel(private val repository : Repository) : ViewModel() {

    //state of Pane : open or close, to display the right fragment
    private var selectedStateFlow = MutableStateFlow<String?>(null)

    private val _eventsFlow = Channel<Event>()
    val eventsFlow = _eventsFlow.receiveAsFlow()



    @OptIn(ExperimentalCoroutinesApi::class)
    val detailState : LiveData<RealEstateDetailViewState?> = selectedStateFlow
        .flatMapLatest { id ->
        if(id != null){
            repository.getOneRealEstates(id).map{
                realEstate -> RealEstateDetailViewState(
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
                amenities = realEstate.amenities)

            }
        }else{
            flowOf(null)
        }
    }.asLiveData()

    fun onRealEstateClick(id: String) {
        selectedStateFlow.value = id
    }

    fun onDetailClosed() {
        selectedStateFlow.value = null
    }

    val listState: LiveData<List<ItemState>> = repository.getAllRealEstates().combine(selectedStateFlow) { listRealEstate, idSelected ->
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

    fun togglePane(){
//        _isPaneOpen.value = !_isPaneOpen.value!!
    _eventsFlow.trySend(Event.OpenDetails)
    }

    fun  isDetailOpen(): Boolean {
        return selectedStateFlow.value != null

    }

}


/**
 * State for items on the recyclerView used on the list view
 */
data class ItemState(
    val isSelected: Boolean,
    val realEstate : RealEstateViewState
)

/**
 * State for real estate infos used in the ItemState
 */
data class RealEstateViewState(
    val id: String,
    val title: String,
    val city : String,
    val priceTag: Float,
    val type: BuildingType,
    val photos : List<Photo>,
    val surface : Int,
    val rooms : Int,
    val bathrooms : Int,
    val bedrooms : Int,
    val description : String,
    val address : String,
    val status : Status,
    val amenities : List<Amenity>
)

/**
 * State for the detail view
 */
data class RealEstateDetailViewState(
    val id: String,
    val title: String,
    val city : String,
    val priceTag: Float,
    val type: BuildingType,
    val photos : List<PhotoSelectedViewState>,
    val surface : Int,
    val rooms : Int,
    val bathrooms : Int,
    val bedrooms : Int,
    val description : String,
    val address : String,
    val status : Status,
    val amenities : List<Amenity>
)

/**
 * To deal with events
 */
//sealed class Event{
//    data object OpenDetails(): Event
//}
sealed interface Event {
    data object OpenDetails : Event
}