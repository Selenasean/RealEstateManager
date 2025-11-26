package com.openclassrooms.realestatemanager.data

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.openclassrooms.realestatemanager.UtilsForInstrumentalTests
import com.openclassrooms.realestatemanager.data.model.RealEstateAgentDb
import com.openclassrooms.realestatemanager.provider.RealEstateContentProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(AndroidJUnit4::class)
class ContentProviderTest {

    private lateinit var database: AppDataBase
    private lateinit var contentResolver: ContentResolver
    private lateinit var realEstateContentUri: Uri

    //sample data to prepopulate the database
    private val fakeAgent = RealEstateAgentDb(id = 1L, name = "John Doe")
    private val realEstate1 = UtilsForInstrumentalTests.fakeRealEstateDb(1, 123)
    private val realEstate2 = UtilsForInstrumentalTests.fakeRealEstateDb(2, 987)

    @Before
    fun createDb(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        //create database
        database = Room.inMemoryDatabaseBuilder(context, AppDataBase::class.java)
            .allowMainThreadQueries()
            .build()

        contentResolver = context.contentResolver
        realEstateContentUri = RealEstateContentProvider.CONTENT_URI

        runBlocking {
            database.realEstateAgentDao().createAgent(fakeAgent)
            database.realEstateDao().createRealEstate(realEstate1)
            database.realEstateDao().createRealEstate(realEstate2)
        }
    }

    @After
    fun closeDb(){
        database.close()
    }

    @Test
    fun read_realestate_with_cursor(){
       //WHEN
        val cursor = contentResolver.query(
            realEstateContentUri,
            null,
            null,
            null,
            null
        )
        //THEN
        assertThat(cursor).isNotNull()
        assertThat(cursor!!.count).isEqualTo(2)

        cursor.close()
    }
}