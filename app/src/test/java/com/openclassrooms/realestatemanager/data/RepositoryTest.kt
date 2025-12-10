package com.openclassrooms.realestatemanager.data


import android.database.Cursor
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.openclassrooms.realestatemanager.TestUtils.UtilsForUnitTests.fakeAgentDb
import com.openclassrooms.realestatemanager.TestUtils.UtilsForUnitTests.fakeFilters
import com.openclassrooms.realestatemanager.TestUtils.UtilsForUnitTests.fakePhoto
import com.openclassrooms.realestatemanager.TestUtils.UtilsForUnitTests.fakeRealEstateDb
import com.openclassrooms.realestatemanager.data.dao.PhotoDao
import com.openclassrooms.realestatemanager.data.dao.RealEstateAgentDao
import com.openclassrooms.realestatemanager.data.dao.RealEstateDao
import com.openclassrooms.realestatemanager.data.model.Amenity
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.PhotoDb
import com.openclassrooms.realestatemanager.data.model.RealEstateDb
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.domain.PhotoChanges
import com.openclassrooms.realestatemanager.domain.RealEstateToCreate
import com.openclassrooms.realestatemanager.domain.RealEstateToUpdate
import com.openclassrooms.realestatemanager.domain.toRealEstate
import com.openclassrooms.realestatemanager.domain.toRealEstateAgent
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class RepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    //Mocks of repository's arguments
    @MockK
    private lateinit var mockRealEstateDao: RealEstateDao

    @MockK
    private lateinit var mockPhotoDao: PhotoDao

    @MockK
    private lateinit var mockAgentDao: RealEstateAgentDao

    @MockK
    private lateinit var mockGeocoderRepository: GeocoderRepository


    private lateinit var repository: Repository

    //Samples for tests
    private val photoDb1 = PhotoDb(
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
    private val realEstateDb1 = fakeRealEstateDb(
        id = 1L,
        type = BuildingType.APARTMENT
    )

    private val realEstateDb2 = fakeRealEstateDb(
        id = 2L,
        type = BuildingType.HOUSE
    )

    private val realEstate1 = mapOf(
        realEstateDb1 to listOf(photoDb1, photoDb2)
    ).toRealEstate().first()

    private val realEstate2 = mapOf(
        realEstateDb2 to listOf(photoDb3)
    ).toRealEstate().first()

    private val fakeAgentDb1 = fakeAgentDb(id = 1L, name = "Agent1")
    private val fakeAgentDb2 = fakeAgentDb(id = 2L, name = "Agent2")
    private val fakeAgentDb3 = fakeAgentDb(id = 3L, name = "Agent3")

    private val fakePosition = Position(48.849998, 2.56667)

    private val dateOfSale = Clock.fixed(Instant.parse(
        "2025-09-14T13:00:00Z"),
        ZoneOffset.UTC)
    private val fakeRealEstateToCreate = RealEstateToCreate(
        type = BuildingType.APARTMENT,
        photos = listOf(
            fakePhoto(
                id = photoDb3.id,
                urlPhoto = photoDb3.urlPhoto,
                label = photoDb3.label
            ),
            fakePhoto(
                id = photoDb2.id,
                urlPhoto = photoDb2.urlPhoto,
                label = photoDb2.label
            )
        ),
        address = "123 Main Street",
        city = "Floride",
        price = 300000F,
        surface = 120,
        rooms = 1,
        bedrooms = 3,
        bathrooms = 2,
        description = "blablablablablabla",
        amenities = listOf(Amenity.BAKERY, Amenity.SCHOOL),
        agentId = 1L
    )

    private val realEstateIdToUpdate = realEstate1.id

    private val photoToCreate = fakePhoto(
        id = photoDb3.id,
        urlPhoto = photoDb3.urlPhoto,
        label = photoDb3.label
    )
    private val photoToUpdate = fakePhoto(
        id = realEstate1.photos[1].id,
        urlPhoto = realEstate1.photos[1].urlPhoto,
        label = "label is modified"
    )
    private val photoToDeleteId = realEstate1.photos[0].id

    private val fakeRealEstateToUpdate = RealEstateToUpdate(
        id = realEstateIdToUpdate.toLong(),
        type = BuildingType.APARTMENT,// this is what we want to change from RealEstate2
        address = realEstate1.address,
        city = realEstate1.city,
        price = realEstate1.priceTag,
        surface = realEstate1.surface,
        rooms = realEstate1.rooms,
        bedrooms = realEstate1.bedrooms,
        bathrooms = realEstate1.bathrooms,
        description = realEstate1.description,
        amenities = realEstate1.amenities,
        status = realEstate1.status,
        dateOfSale = realEstate1.dateOfSale,
        dateOfCreation = realEstate1.dateCreated,
        agentId = realEstate1.agentId,
        photoChanges = PhotoChanges(
            deleted = listOf(photoToDeleteId),
            created = listOf(photoToCreate),
            updated = listOf(photoToUpdate)
        )//this is the changes in photos
    )


    private val realEstateDbSlot = slot<RealEstateDb>()
    private val photoUpdateSlot = slot<PhotoDb>()
    private val photoDbSlot = slot<PhotoDb>()


    @Before
    fun setup() {
        repository = Repository(
            photoDao = mockPhotoDao,
            realEstateDao = mockRealEstateDao,
            realEstateAgentDao = mockAgentDao,
            geocoderRepository = mockGeocoderRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() //reset main dispatcher
    }

    @Test
    fun get_all_realEstates_succeed_with_no_filter() = runTest {
        //GIVEN
        val daoResult: Map<RealEstateDb, List<PhotoDb>> = mapOf(
            realEstateDb1 to listOf(photoDb1, photoDb2),
            realEstateDb2 to listOf(photoDb3)
        )
        coEvery {
            mockRealEstateDao.getAllRealEstates(
                city = null,
                type = BuildingType.entries,
                minPrice = null,
                maxPrice = null,
                minSurface = null,
                maxSurface = null,
                status = null
            )
        } returns flowOf(daoResult)

        val listExpected = listOf(realEstate1, realEstate2)

        //WHEN
        repository
            .getAllRealEstates(
                filter = fakeFilters(
                    type = BuildingType.entries
                )
            )
            .collect { listEmitted ->
                //THEN
                assertThat(listEmitted).isEqualTo(listExpected)
            }
    }

    @Test
    fun get_all_filtered_realEstates_succeed_according_filters() = runTest {
        //GIVEN
        val daoResultFiltered: Map<RealEstateDb, List<PhotoDb>> = mapOf(
            realEstateDb1 to listOf(photoDb1, photoDb2),
        )
        coEvery {
            mockRealEstateDao.getAllRealEstates(
                city = null,
                type = listOf(BuildingType.HOUSE),
                minPrice = null,
                maxPrice = null,
                minSurface = null,
                maxSurface = null,
                status = null
            )
        } returns flowOf(daoResultFiltered)

        //WHEN
        repository
            .getAllRealEstates(
                filter = fakeFilters(
                    type = listOf(BuildingType.HOUSE)
                )
            )
            .test {
                val listEmitted = awaitItem()
                //THEN
                assertThat(listEmitted.contains(realEstate1))
                assertThat(listEmitted.size).isEqualTo(1)
                cancelAndConsumeRemainingEvents()
            }


    }

    @Test
    fun get_only_one_realEstate_by_Id_succeed() = runTest {
        //GIVEN
        val daoOneResult: Map<RealEstateDb, List<PhotoDb>> = mapOf(
            realEstateDb1 to listOf(photoDb1, photoDb2),
        )
        coEvery {
            mockRealEstateDao.getOneRealEstate(realEstateId = 1)
        } returns flowOf(daoOneResult)

        //WHEN
        repository.getOneRealEstate(realEstateId = "1").test {
            val realEstate = awaitItem()
            //THEN
            assertThat(realEstate).isEqualTo(realEstate1)
        awaitComplete()
        }

    }

    @Test
    fun fetch_all_agents() = runTest {
        //GIVEN
        val daoAgentResult = listOf(
            fakeAgentDb1,
            fakeAgentDb2,
            fakeAgentDb3
        )
        coEvery {
            mockAgentDao.fetchAllAgents()
        } returns daoAgentResult
        //WHEN
        val allAgents = repository.fetchAllAgents()
        //THEN
        assertThat(allAgents).isEqualTo(
            listOf(
                fakeAgentDb(id = 1L, name = "Agent1").toRealEstateAgent(),
                fakeAgentDb(id = 2L, name = "Agent2").toRealEstateAgent(),
                fakeAgentDb(id = 3L, name = "Agent3").toRealEstateAgent()
            )
        )
    }

    @Test
    fun fetch_one_agent() = runTest {
        //GIVEN
        coEvery {
            mockAgentDao.fetchOneAgent(any())
        } returns fakeAgentDb2
        //WHEN
        repository.fetchOneAgent(2)
        //THEN
        coVerify {
            mockAgentDao.fetchOneAgent(2)
        }
    }

    @Test
    fun create_realEstate_succeed() = runTest {
        //GIVEN
        val expectedCreatedId = 1234L
        //geocoder will always returns a valid position
        coEvery {
            mockGeocoderRepository.getLongLat(fakeRealEstateToCreate.address)
        } returns fakePosition

        //get the realEstate with slot
        val realEstateDbSlot = slot<RealEstateDb>()
        //creation will return a id if it succeed to create the realEstate
        coEvery {
            mockRealEstateDao.createRealEstate(capture(realEstateDbSlot))
        } returns expectedCreatedId
        //run to capture the argument
        coEvery {
            mockPhotoDao.createPhoto(any())
        } just Runs

        val allCapturedPhotos = mutableListOf<PhotoDb>()
        coEvery {
            mockPhotoDao.createPhoto(any())
        } answers {
            allCapturedPhotos.add(arg(0) as PhotoDb)
        }

        //WHEN
        val result = repository.createRealEstate(fakeRealEstateToCreate)

        //THEN
        assertThat(SaveResult.SUCCESS).isEqualTo(result)
        //verify fakeRealEstate data is correctly created
        val capturedRealEstate = realEstateDbSlot.captured
        assertThat(capturedRealEstate.id).isEqualTo(0L)
        assertThat(capturedRealEstate.type).isEqualTo(fakeRealEstateToCreate.type)
        assertThat(capturedRealEstate.address).isEqualTo(fakeRealEstateToCreate.address)
        assertThat(capturedRealEstate.city).isEqualTo(fakeRealEstateToCreate.city)
        assertThat(capturedRealEstate.price).isEqualTo(fakeRealEstateToCreate.price)
        assertThat(capturedRealEstate.surface).isEqualTo(fakeRealEstateToCreate.surface)
        assertThat(capturedRealEstate.rooms).isEqualTo(fakeRealEstateToCreate.rooms)
        assertThat(capturedRealEstate.bedrooms).isEqualTo(fakeRealEstateToCreate.bedrooms)
        assertThat(capturedRealEstate.bathrooms).isEqualTo(fakeRealEstateToCreate.bathrooms)
        assertThat(capturedRealEstate.description).isEqualTo(fakeRealEstateToCreate.description)
        assertThat(capturedRealEstate.amenities).isEqualTo(fakeRealEstateToCreate.amenities)
        assertThat(capturedRealEstate.realEstateAgentId).isEqualTo(fakeRealEstateToCreate.agentId)


        //verify the photoDAO was called 2 times
        coVerify(exactly = fakeRealEstateToCreate.photos.size) {
            mockPhotoDao.createPhoto(any())
        }
    }

    @Test
    fun create_realEstate_failed() = runTest {
        //GIVEN
        coEvery {
            mockGeocoderRepository.getLongLat(any())
        } returns null
        //WHEN
        val result = repository.createRealEstate(fakeRealEstateToCreate)
        //THEN
        assertThat(result).isEqualTo(SaveResult.ERROR)
    }

    @Test
    fun update_realEstate_succeed() = runTest {
        //GIVEN
        //geocoder will always returns a valid position
        coEvery {
            mockGeocoderRepository.getLongLat(fakeRealEstateToUpdate.address)
        } returns fakePosition

        coEvery {
            mockRealEstateDao.updateRealEstate(capture(realEstateDbSlot))
        } just Runs
        coEvery {
            mockPhotoDao.deleteFromId(any())
        } just Runs
        coEvery {
            mockPhotoDao.createPhoto(capture(photoDbSlot))
        } just Runs
        coEvery {
            mockPhotoDao.updatePhoto(capture(photoUpdateSlot))
        } just Runs

        //WHEN
        val result = repository.updateRealEstate(
            realEstate = fakeRealEstateToUpdate
        )

        //THEN
        //1. verify update is called once and succeed
        assertThat(result).isEqualTo(SaveResult.SUCCESS)

        val capturedRealEstateToUpdate = realEstateDbSlot.captured
        assertThat(capturedRealEstateToUpdate.type).isEqualTo(fakeRealEstateToUpdate.type)

        //2.verify photo is deleted
        coVerify {
            mockPhotoDao.deleteFromId(photoToDeleteId)
        }

        //3. verify photo label is changed
        val capturedUpdatedPhoto = photoUpdateSlot.captured
        assertThat(capturedUpdatedPhoto.id).isEqualTo(realEstate1.photos[1].id)

        //4. verify photo is created
        val capturedPhotoCreated = photoDbSlot.captured
        assertThat(capturedPhotoCreated.id).isEqualTo(photoDb3.id)

    }

    @Test
    fun update_realEstate_failed() = runTest {
        //GIVEN
        coEvery {
            mockGeocoderRepository.getLongLat(fakeRealEstateToUpdate.address)
        } returns null
        //WHEN
        val result = repository.updateRealEstate(fakeRealEstateToUpdate)
        //THEN
        assertThat(result).isEqualTo(SaveResult.ERROR)
    }


}
