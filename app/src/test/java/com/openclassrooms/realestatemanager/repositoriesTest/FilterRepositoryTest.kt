package com.openclassrooms.realestatemanager.repositoriesTest

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.openclassrooms.realestatemanager.data.FilterRepository
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.domain.Filters
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class FilterRepositoryTest {

    // Creates a temporary folder that is deleted after each test.
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val testScope =
        TestScope(UnconfinedTestDispatcher()) //use Unconfined to immediate execution
    private lateinit var filterRepository: FilterRepository

    @Before
    fun setup() {
        val dataStoreFile = temporaryFolder.newFile("test_filters.preferences_pb")
        // "Temporary" hack due to an issue with DataStore on Windows
        // https://github.com/android/codelab-android-datastore/issues/48#issuecomment-1960867652
        // https://issuetracker.google.com/issues/203087070
        dataStoreFile.delete()
        val dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { dataStoreFile },
        )
        filterRepository = FilterRepository(dataStore)
    }

    @After
    fun tearDown() {
        // Cancel the scope to ensure DataStore coroutines are stopped
        testScope.cancel()
    }

    @Test
    fun initial_state() = testScope.runTest { // Use the testScope we created
        filterRepository.getFilters().test {
            val filters = awaitItem()
            // assert that the initial value inside the Datastore is the default filters values
            assertThat(filters.city).isNull()
            assertThat(filters.type).isEmpty()
            assertThat(filters.priceMin).isNull()
            assertThat(filters.priceMax).isNull()
            assertThat(filters.surfaceMin).isNull()
            assertThat(filters.surfaceMax).isNull()
            assertThat(filters.status).isNull()
        }
    }

    @Test
    fun setFilters() = testScope.runTest {
        //GIVEN
        val filtersToSet = Filters(
            city = "serris",
            type = listOf(BuildingType.HOUSE, BuildingType.VILA),
            priceMin = null,
            priceMax = null,
            surfaceMin = null,
            surfaceMax = null,
            status = Status.FOR_SALE
        )

        //WHEN
        filterRepository.setFilters(filtersToSet)

        //THEN
        //verify getFilters emit the filters updated
        filterRepository.getFilters().test {
            val valueEmitted = awaitItem()
            assertThat(valueEmitted).isEqualTo(filtersToSet)
        }
    }


}