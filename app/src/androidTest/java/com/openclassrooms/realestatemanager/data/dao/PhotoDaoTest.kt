package com.openclassrooms.realestatemanager.data.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import assertk.all
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.containsOnly
import assertk.assertions.doesNotContain
import assertk.assertions.extracting
import assertk.assertions.hasSize
import com.openclassrooms.realestatemanager.AppApplication
import com.openclassrooms.realestatemanager.UtilsForInstrumentalTests
import com.openclassrooms.realestatemanager.data.AppDataBase
import com.openclassrooms.realestatemanager.data.model.PhotoDb
import com.openclassrooms.realestatemanager.data.model.RealEstateAgentDb
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PhotoDaoTest {

    private lateinit var database: AppDataBase
    private lateinit var photoDao: PhotoDao
    private lateinit var realEstateDao: RealEstateDao
    private lateinit var agentDao: RealEstateAgentDao
    private var realEstateId1: Long = 0
    private var agentId1: Long = 0
    private lateinit var photoToCreate: PhotoDb
    private lateinit var photoCreated2: PhotoDb

    @Before
    fun createDatabase() {
        database = Room
            .inMemoryDatabaseBuilder(AppApplication.Companion.appContext, AppDataBase::class.java)
            .build()

        photoDao = database.photoDao()
        realEstateDao = database.realEstateDao()
        agentDao = database.realEstateAgentDao()

        //create few items in database
        agentId1 = agentDao.createAgent(
            RealEstateAgentDb(
                id = 1,
                name = "Josie"
            )
        )
        runTest {
            realEstateId1 = realEstateDao.createRealEstate(
                UtilsForInstrumentalTests.fakeRealEstateDb(0)
            )
        }
        photoToCreate = PhotoDb(
            id = "1",
            realEstateId = realEstateId1,
            urlPhoto = "https://images.unsp" +
                    "lash.com/photo-1504615755583-2916b52192a3?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OHx8aG91c2V8ZW58MHx8MHx8fDA%3D",
            label = "photo1"
        )
        photoCreated2 = PhotoDb(
            id = "2",
            realEstateId = realEstateId1,
            urlPhoto = "https://images.unsp" +
                    "lash.com/photo-1504615755583-2916b52192a3?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OHx8aG91c2V8ZW58MHx8MHx8fDA%3D",
            label = "photo2"
        )

    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun create_photo_should_add_a_new_photo_in_database() = runTest {

        //WHEN
        photoDao.createPhoto(photoToCreate)

        //THEN
        photoDao.getAllPhotos().test {
            val valueEmitted = awaitItem()
            assertThat(valueEmitted).all {
                containsOnly(photoToCreate)
            }
        }
    }

    @Test
    fun delete_photo_should_remove_photo_from_database() = runTest {
        //GIVEN
        photoDao.createPhoto(photoToCreate)
        photoDao.createPhoto(photoCreated2)
        //WHEN
        photoDao.deletePhoto(photoToCreate)
        //THEN
        //verify that there is only one photo in database
        photoDao.getAllPhotos().test {
            val valueEmitted = awaitItem()
            assertThat(valueEmitted).all {
                doesNotContain(photoToCreate)
                hasSize(1)
            }
        }
    }

    @Test
    fun delete_photo_from_id_should_remove_specific_photo_from_database() = runTest {
        //GIVEN
        photoDao.createPhoto(photoToCreate)
        photoDao.createPhoto(photoCreated2)
        //WHEN
        photoDao.deleteFromId(photoCreated2.id)
        //THEN
        photoDao.getAllPhotos().test {
            val valueEmitted = awaitItem()
            assertThat(valueEmitted).all {
                doesNotContain(photoCreated2)
                hasSize(1)
            }
        }
    }


    @Test
    fun update_photo_should_update_photo_label() = runTest {
        //GIVEN
        photoDao.createPhoto(photoCreated2)
        //update one  : photoCreated2
        val photoUpdated = PhotoDb(
            id = photoCreated2.id,
            realEstateId = photoCreated2.realEstateId,
            urlPhoto = photoCreated2.urlPhoto,
            label = "label changed"//what's modified
        )

        //WHEN
        photoDao.updatePhoto(photoUpdated)

        //THEN
        photoDao.getAllPhotos().test {
            val valueEmitted = awaitItem()
            assertThat(valueEmitted)
                .extracting(PhotoDb::label)
                .containsExactly("label changed")
        }
    }
}