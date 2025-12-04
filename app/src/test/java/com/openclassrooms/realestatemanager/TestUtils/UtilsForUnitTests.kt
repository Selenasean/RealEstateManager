package com.openclassrooms.realestatemanager.TestUtils

import com.openclassrooms.realestatemanager.data.model.Amenity
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.RealEstateAgentDb
import com.openclassrooms.realestatemanager.data.model.RealEstateDb
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.domain.Filters
import com.openclassrooms.realestatemanager.domain.Photo
import com.openclassrooms.realestatemanager.domain.RealEstate
import com.openclassrooms.realestatemanager.ui.filter.FilterState
import com.openclassrooms.realestatemanager.ui.list_map_details.ItemState
import com.openclassrooms.realestatemanager.ui.list_map_details.MapState
import com.openclassrooms.realestatemanager.ui.list_map_details.RealEstateDetailViewState
import com.openclassrooms.realestatemanager.ui.list_map_details.RealEstateViewState
import io.mockk.internalSubstitute
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import kotlin.Boolean
import kotlin.time.toKotlinInstant

object UtilsForUnitTests {

    fun fakeRealEstateDb(
        id: Long,
        name: String = "realEstate",
        city: String = "Paris",
        price: Float = 500000F,
        type: BuildingType = BuildingType.APARTMENT,
        surface: Int = 90
    ): RealEstateDb {
        return RealEstateDb(
            id = id,
            name = name,
            city = city,
            price = price,
            type = type,
            surface = surface,
            rooms = 2,
            bathrooms = 1,
            bedrooms = 2,
            description = "Appartment near Eiffel Tower",
            address = "219 rue de l'Université",
            status = Status.FOR_SALE,
            amenities = listOf(Amenity.BAKERY, Amenity.SCHOOL, Amenity.STATION),
            latitude = 48.862725,
            longitude = 2.287592,
            realEstateAgentId = 1,
            dateCreated = LocalDateTime.of(LocalDate.of(2025, 8, 28), LocalTime.of(15, 0))
                .toInstant(ZoneOffset.UTC),
            dateOfSale = null,
            video = null
        )
    }

    fun fakeFilters(
        type: List<BuildingType>
    ): Filters {
        return Filters(
            city = null,
            type = type,
            priceMin = null,
            priceMax = null,
            surfaceMin = null,
            surfaceMax = null,
            status = null
        )
    }

    fun fakeAgentDb(
        id: Long,
        name: String
    ): RealEstateAgentDb {
        return RealEstateAgentDb(
            id = id,
            name = name
        )
    }

    fun fakePhoto(
        id: String,
        urlPhoto: String,
        label: String
    ): Photo {
        return Photo(
            id = id,
            urlPhoto = urlPhoto,
            label = label
        )
    }

    fun fakeRealEstate(id: String, type: BuildingType = BuildingType.APARTMENT): RealEstate{
        return RealEstate(
            id = id,
            title = "TODO()",
            city = "TODO()",
            priceTag = 1f,
            type = type,
            photos = emptyList(),
            surface = 23,
            rooms = 2,
            bathrooms = 2,
            bedrooms = 2,
            description = "TODO()",
            address = "TODO()",
            status = Status.FOR_SALE,
            amenities = emptyList(),
            latitude = 48.862725,
            longitude = 2.287592,
            agentId = 2,
            dateCreated = LocalDateTime.of(LocalDate.of(2025, 8, 28), LocalTime.of(15, 0))
                .toInstant(ZoneOffset.UTC),
            dateOfSale =null,
            video = null
        )
    }

    fun fakeRealEstateDetailState(id: String,type: BuildingType = BuildingType.APARTMENT): RealEstateDetailViewState{
        return RealEstateDetailViewState(
            id = id,
            title = "TODO()",
            city = "TODO()",
            priceTag = 1f,
            type = type,
            photos = emptyList(),
            surface = 23,
            rooms = 2,
            bathrooms = 2,
            bedrooms = 2,
            description = "TODO()",
            address = "TODO()",
            status =Status.FOR_SALE,
            amenities = emptyList(),
            latitude = 48.862725,
            longitude = 2.287592,
            dateOfSale = null,
            video = null
        )

    }

    fun fakeItemState(isSelected: Boolean = false, fakeRealEstate: RealEstate): ItemState{
        return ItemState(
            isSelected = isSelected,
            realEstate = RealEstateViewState(
                id = fakeRealEstate.id,
                title = fakeRealEstate.title,
                city = fakeRealEstate.city,
                priceTag = fakeRealEstate.priceTag,
                type = fakeRealEstate.type,
                photos = fakeRealEstate.photos,
                surface = fakeRealEstate.surface,
                rooms = fakeRealEstate.rooms,
                bathrooms = fakeRealEstate.bathrooms,
                bedrooms = fakeRealEstate.bedrooms,
                description = fakeRealEstate.description,
                address = fakeRealEstate.address,
                status = fakeRealEstate.status,
                amenities = fakeRealEstate.amenities,
                dateOfSale = null
            )
        )
    }

    fun fakeMapState(isSelected: Boolean = false, fakeRealEstate: RealEstate): MapState{
        return MapState(
            id = fakeRealEstate.id,
            type = fakeRealEstate.type,
            city = fakeRealEstate.city,
            priceTag = fakeRealEstate.priceTag,
            isSelected = isSelected,
            status = fakeRealEstate.status,
            longitude = fakeRealEstate.longitude,
            latitude = fakeRealEstate.latitude
        )
    }

    fun fakeFilterState(types: List<BuildingType>): FilterState {
        return FilterState(
            city = null,
            type = types,
            priceMin = null,
            priceMax = null,
            surfaceMin = null,
            surfaceMax = null,
            status = null
        )
    }

}