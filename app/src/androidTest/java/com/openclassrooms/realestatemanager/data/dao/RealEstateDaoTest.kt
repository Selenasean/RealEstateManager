package com.openclassrooms.realestatemanager.data.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.extracting
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isSameAs
import assertk.assertions.isTrue
import com.openclassrooms.realestatemanager.AppApplication
import com.openclassrooms.realestatemanager.UtilsForInstrumentalTests
import com.openclassrooms.realestatemanager.data.AppDataBase
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.RealEstateAgentDb
import com.openclassrooms.realestatemanager.data.model.RealEstateDb
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class RealEstateDaoTest {

    private lateinit var database: AppDataBase
    private lateinit var realEstateDao: RealEstateDao
    private lateinit var photoDao: PhotoDao
    private lateinit var agentDao: RealEstateAgentDao
    private var agentId1: Long = 0

    //sample for test
    private lateinit var realEstateToCreate: RealEstateDb
    private lateinit var realEstateToCreate2: RealEstateDb
    private var generateId1: Long = 0
    private var generateId2: Long = 0

    @Before
    fun createDatabase()= runTest {
        database = Room
            .inMemoryDatabaseBuilder(AppApplication.appContext, AppDataBase::class.java)
            .build()

        realEstateDao = database.realEstateDao()
        photoDao = database.photoDao()
        agentDao = database.realEstateAgentDao()

        //create few items in database
        agentId1 = agentDao.createAgent(
            RealEstateAgentDb(
                id = 1,
                name = "Josie"
            )
        )

        realEstateToCreate = UtilsForInstrumentalTests.fakeRealEstateDb(id = 0, surface = 123, agentId1)
        realEstateToCreate2 = UtilsForInstrumentalTests.fakeRealEstateDb(id = 0, surface = 550, agentId1)

        generateId1 = realEstateDao.createRealEstate(realEstateToCreate)
        generateId2 = realEstateDao.createRealEstate(realEstateToCreate2)

    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun create_realestate_should_insert_in_db() = runTest {
        //WHEN
        //THEN
        //generate Id by room should be positive
        assertThat(generateId1 > 0).isTrue()
        //find the new realestate created
        realEstateDao.getOneRealEstate(generateId1).test {
            val valueEmitted = awaitItem()
            //there is one item
            assertThat(valueEmitted.size).isEqualTo(1)

            //check if ids matches
            val (emittedRealEstate, photos) = valueEmitted.entries.first()
            assertThat(emittedRealEstate.id).isEqualTo(generateId1)
        }

    }

    @Test
    fun delete_should_remove_from_db() = runTest {
        //WHEN
        val realEstateToDelete = UtilsForInstrumentalTests.fakeRealEstateDb(id = generateId1)
        realEstateDao.deleteRealEstate(realEstateToDelete)

        //THEN
        //get all realEstates without filters
        realEstateDao.getAllRealEstates(
            city = null,
            type = BuildingType.entries,
            minPrice = null,
            maxPrice = null,
            minSurface = null,
            maxSurface = null,
            status = null
        ).test {
            val emittedValue = awaitItem()
            //only one item is left
            assertThat(emittedValue.size).isEqualTo(1)
            //check if id's item left is correct
            val (realEstateEmitted, photos) = emittedValue.entries.first()
            assertThat(realEstateEmitted.id).isEqualTo(generateId2)
        }
    }

    @Test
    fun update_should_modify_existing_realestate_in_db() = runTest {
        //GIVEN
        val realEstateUpdated =
            UtilsForInstrumentalTests.fakeRealEstateDb(id = generateId1, surface = 1000)
        //WHEN
        realEstateDao.updateRealEstate(realEstateUpdated)

        //THEN
        realEstateDao.getOneRealEstate(generateId1).test {
            val valueEmitted = awaitItem()
            val realEstateEmitted = valueEmitted.entries.first()
            assertThat(realEstateEmitted.key.surface).isEqualTo(1000)
        }
    }

    @Test
    fun get_all_realestate_with_no_filters_should_return_all()= runTest {
        //GIVEN
        //WHEN get with no filters
        realEstateDao.getAllRealEstates(
            city = null,
            type = BuildingType.entries,
            minPrice = null,
            maxPrice = null,
            minSurface = null,
            maxSurface = null,
            status = null
        ).test {
            //THEN
            val valueEmitted = awaitItem()
            assertThat(valueEmitted.keys)
                .extracting(RealEstateDb::id)
                .containsExactly(generateId1, generateId2)
        }
    }

    @Test
    fun get_all_realestate_with_filters_should_return_specific_realestate()= runTest {
        //GIVEN
        //WHEN
        realEstateDao.getAllRealEstates(
            city = null,
            type = BuildingType.entries,
            minPrice = null,
            maxPrice = null,
            minSurface = 500,
            maxSurface = null,
            status = null
        ).test {
            val valueEmitted = awaitItem()
            //THEN
            assertThat(valueEmitted.size).isEqualTo(1)
            assertThat(valueEmitted.keys.first().id).isEqualTo(generateId2)
        }

    }

    @Test
    fun get_all_realestates_with_cursor(){
        //GIVEN
        val returnedIds = mutableListOf<Long>()
        //WHEN
        val cursorResult = realEstateDao.getAllRealEstatesWithCursor()

        //THEN
        assertThat(cursorResult).isNotNull()
        assertThat(cursorResult.count).isEqualTo(2)
        assertThat(cursorResult.moveToFirst()).isTrue()
        if(cursorResult.moveToFirst()){
            do{
                val id = cursorResult.getLong(cursorResult.getColumnIndexOrThrow("id"))
                returnedIds.add(id)
            } while(cursorResult.moveToNext())
        }
        assertThat(returnedIds).containsExactly(generateId1, generateId2)
        cursorResult.close()
    }

    @Test
    fun get_one_realestate_with_cursor(){
        //WHEN
        val cursorResult = realEstateDao.getOneRealEstateWithCursor(generateId2)
        //THEN
        assertThat(cursorResult).isNotNull()
        assertThat(cursorResult.count).isEqualTo(1)
        assertThat(cursorResult.moveToFirst()).isTrue()
        val idColumnIndex = cursorResult.getColumnIndexOrThrow("id")
        val actualId = cursorResult.getLong(idColumnIndex)
        assertThat(actualId).isEqualTo(generateId2)
        cursorResult.close()
    }

}