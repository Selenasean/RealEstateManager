package com.openclassrooms.realestatemanager

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.openclassrooms.realestatemanager.data.GeocoderRepository
import com.openclassrooms.realestatemanager.data.Repository
import com.openclassrooms.realestatemanager.data.dao.PhotoDao
import com.openclassrooms.realestatemanager.data.dao.RealEstateAgentDao
import com.openclassrooms.realestatemanager.data.dao.RealEstateDao
import com.openclassrooms.realestatemanager.data.model.Amenity
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.PhotoDb
import com.openclassrooms.realestatemanager.data.model.RealEstateDb
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.domain.toRealEstate
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
public class RepositoryTest {


    @Mock
    private lateinit var mockRealEstateDao: RealEstateDao
    @Mock
    private lateinit var mockPhotoDao: PhotoDao
    @Mock
    private lateinit var mockAgentDao: RealEstateAgentDao
    @Mock
    private lateinit var mockGeocoderRepository: GeocoderRepository

    private lateinit var repository: Repository

    private val photoDb1= PhotoDb(
        id = "photo1",
        1L,
        urlPhoto = "url1.jpg",
        label = "kitchen"
    )
    private val photoDb2 = PhotoDb(
        id = "photo2",
        realEstateId = 1L,
        urlPhoto = "url2.jpg",
        label = "room"
    )
    private val photoDb3 = PhotoDb(
        id = "photo3",
        realEstateId = 2L,
        urlPhoto = "url3.jpg",
        label = "kitchen"
    )
    private val realEstateDb1 = fakeRealEstateDb(id = 1)

    private val realEstateDb2 = fakeRealEstateDb(id = 2)

    private val realEstate1 = mapOf(
        realEstateDb1 to listOf(photoDb1, photoDb2)
    ).toRealEstate().first()

    private val realEstate2 = mapOf(
        realEstateDb2 to listOf(photoDb3)
    ).toRealEstate().first()

    @Before
    fun setup(){
        MockitoAnnotations.openMocks(this)
        repository = Repository(
            photoDao = mockPhotoDao,
            realEstateDao = mockRealEstateDao,
            realEstateAgentDao = mockAgentDao,
            geocoderRepository = mockGeocoderRepository
        )
    }

    @Test
//    fun get_all_realEstates_succeed() = runTest {
//        //Given
//        val daoResult: Map<RealEstateDb, List<PhotoDb>> = mapOf(
//            realEstateDb1 to listOf(photoDb1,photoDb2),
//            realEstateDb2 to listOf(photoDb3)
//        )
//        whenever(mockRealEstateDao.getAllRealEstates()).thenReturn(flowOf(daoResult))
//         val listExpected = listOf(realEstate1, realEstate2)
//
//        //when
//        repository.getAllRealEstates().test {
//            //then
//
//            val listEmitted = awaitItem()
//
//            assertThat(listEmitted).isEqualTo(listExpected)
//
//            awaitComplete()
//        }
//
//
//    }

    @Test
    fun get_only_one_realEstate_succeed() {
    }

    @Test
    fun fetch_all_agents() {
    }

    @Test
    fun create_realEstate_succeed() {
    }

    @Test
    fun update_realEstate_succeed() {
    }

    @Test
    fun get_listOf_realEstates_filtered_succeed() {
    }


}
fun fakeRealEstateDb(
    id: Long,
    name :String = "realEstate",
    city: String = "Paris",
    price : Float = 500000F,
    type: BuildingType = BuildingType.APARTMENT,
    surface: Int = 90
) : RealEstateDb {
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
        dateCreated = LocalDateTime.of(LocalDate.of(2025, 8, 28), LocalTime.of(15, 0)).toInstant(ZoneOffset.UTC),
        dateOfSale = null
    )
}