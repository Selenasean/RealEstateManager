package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.data.model.Amenity
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.RealEstateDb
import com.openclassrooms.realestatemanager.data.model.Status
import java.time.Clock


object UtilsForInstrumentalTests {

    fun fakeRealEstateDb(
        id: Long,
        surface: Int = 123,
        realEstateAgentId: Long = 1
    ) : RealEstateDb{
        return RealEstateDb(
            id = id,
            BuildingType.APARTMENT,
            123456f,
            "",
            surface = surface,
            rooms = 12,
            bathrooms = 2,
            bedrooms = 2,
            description = "blbzlzbkfngvkrj",
            address = "12 allée des petits jules",
            city = "paris",
            status = Status.FOR_SALE,
            amenities = listOf(Amenity.SCHOOL),
            dateCreated = Clock.systemUTC().instant(),
            dateOfSale = null,
            realEstateAgentId = 1,
            longitude = 34.000,
            latitude =34.000,
            video = null
        )
    }

}