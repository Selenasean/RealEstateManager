package com.openclassrooms.realestatemanager.ui.list_map_details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.all
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.extracting
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import com.openclassrooms.realestatemanager.TestUtils.UtilsForUnitTests
import com.openclassrooms.realestatemanager.TestUtils.UtilsForUnitTests.fakeFilters
import com.openclassrooms.realestatemanager.TestUtils.UtilsForUnitTests.fakeItemState
import com.openclassrooms.realestatemanager.TestUtils.UtilsForUnitTests.fakeMapState
import com.openclassrooms.realestatemanager.TestUtils.UtilsForUnitTests.fakeRealEstate
import com.openclassrooms.realestatemanager.data.FilterRepository
import com.openclassrooms.realestatemanager.data.Repository
import com.openclassrooms.realestatemanager.data.location.LocationRepository
import com.openclassrooms.realestatemanager.data.model.BuildingType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class ListMapDetailViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    //Test dispatcher for coroutines
    private val testDispatcher = UnconfinedTestDispatcher()

    //mock of arguments
    @MockK
    private lateinit var mockRepository: Repository

    @MockK
    private lateinit var mockLocationRepository: LocationRepository

    @MockK
    private lateinit var mockFiltersRepository: FilterRepository

    private lateinit var listMapDetailViewModel: ListMapDetailViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private val filtersFlow = MutableStateFlow(fakeFilters(BuildingType.entries))

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle()
        coEvery {
            mockFiltersRepository.getFilters()
        } returns filtersFlow
        coEvery {
            mockRepository.getAllRealEstates(any())
        } returns flowOf(
            listOf(
                fakeRealEstate("3"),
                fakeRealEstate("4")
            )
        )
        val oneRealEstate = MutableStateFlow(fakeRealEstate(id = "2"))
        coEvery {
            mockRepository.getOneRealEstate(any())
        } returns oneRealEstate

        listMapDetailViewModel = ListMapDetailViewModel(
            repository = mockRepository,
            savedStateHandle = savedStateHandle,
            locationRepository = mockLocationRepository,
            filtersRepository = mockFiltersRepository
        )
    }



    @Test
    fun detailState_emit_null_if_no_id_in_savedStateHandle() = runTest {
        //WHEN
        //there is no value in savedStateHandle
        //means it's the list which is display
        listMapDetailViewModel.detailState.test {
            val valueEmitted = awaitItem()
            //THEN
            assertThat(valueEmitted).isEqualTo(null)
        }
    }

    @Test
    fun click_on_realestate_store_id_in_savedStateHandle() = runTest {
        listMapDetailViewModel.onRealEstateClick("2")
        assertThat(savedStateHandle.get<String>(ListMapDetailViewModel.ID_KEY)).isEqualTo("2")
    }

    @Test
    fun detailState_emit_value_when_id_in_savedStateHandle() = runTest {
        //GIVEN
        listMapDetailViewModel.onRealEstateClick("2")
        //WHEN
        listMapDetailViewModel.detailState.test {
            val valueEmitted = awaitItem()
            //THEN
            assertThat(valueEmitted).isEqualTo(UtilsForUnitTests.fakeRealEstateDetailState(id = "2"))
        }

    }


    @Test
    @Suppress("UnusedFlow")
    fun listState_should_called_repository_with_right_filter() = runTest {
        //GIVEN
        //specify a filter
        filtersFlow.value = fakeFilters(listOf(BuildingType.APARTMENT))
        //WHEN
        //observe listState to emit a value
        listMapDetailViewModel.listState.test {
            awaitItem()
        }
        //THEN
        //verify that the repository is called with the right parameter
        coVerify {
            mockRepository.getAllRealEstates(fakeFilters(listOf(BuildingType.APARTMENT)))
        }
    }

    @Test
    fun listState_should_return_map_RealEstate_into_ItemState() = runTest {
        //GIVEN
        val listExpected = listOf(
            fakeItemState(false, fakeRealEstate("3")),
            fakeItemState(false, fakeRealEstate("4"))
        )

        //WHEN
        listMapDetailViewModel.listState.test {
            val valueEmitted = awaitItem()
            //THEN
            assertThat(valueEmitted).all{
                hasSize(2)
                isEqualTo(listExpected)
            }
            assertThat(valueEmitted).extracting{ it.realEstate.id }.containsExactly("3","4")
        }


    }


    @Test
    @Suppress("UnusedFlow")
    fun mapList_should_called_repository_with_right_filter()= runTest {
        //GIVEN
        //specify a filter
        filtersFlow.value = fakeFilters(listOf(BuildingType.APARTMENT))
        //WHEN
        listMapDetailViewModel.mapList.test {
             awaitItem()
        }
        //THEN
        coVerify { mockRepository.getAllRealEstates(fakeFilters(listOf(BuildingType.APARTMENT))) }

    }

    @Test
    fun mapList_should_return_list_of_mapState() = runTest {
        //GIVEN
        val listExpected = listOf(
            fakeMapState(false, fakeRealEstate("3")),
            fakeMapState(false, fakeRealEstate("4"))
        )
        //WHEN
        listMapDetailViewModel.mapList.test {
            val valueEmitted = awaitItem()
            //THEN
            assertThat(valueEmitted).all {
                hasSize(2)
                isEqualTo(listExpected)
            }
            assertThat(valueEmitted).extracting { it.id }.containsExactly("3","4")
        }

    }


}