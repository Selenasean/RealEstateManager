package com.openclassrooms.realestatemanager.ui.filter

import androidx.lifecycle.SavedStateHandle
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.openclassrooms.realestatemanager.TestUtils.UtilsForUnitTests.fakeFilterState
import com.openclassrooms.realestatemanager.TestUtils.UtilsForUnitTests.fakeFilters
import com.openclassrooms.realestatemanager.data.FilterRepository
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.domain.Filters
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class FilterViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    //Test dispatcher for coroutines
    private val testDispatcher = UnconfinedTestDispatcher()

    //mock for argument
    @MockK
    private lateinit var mockFilterRepository: FilterRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var filterViewModel: FilterViewModel

    val filterState = FilterState()

    val emptyFilter = fakeFilters(emptyList())

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        savedStateHandle = SavedStateHandle()

        coEvery {
            mockFilterRepository.getFilters()
        } returns MutableStateFlow(emptyFilter)
        coJustRun{
            mockFilterRepository.setFilters(emptyFilter)
        }

        filterViewModel = FilterViewModel(
            savedStateHandle,
            mockFilterRepository
        )

    }

    @Test
    fun updateType_should_update_state() {
        //GIVEN
        //there is no filters selected
        assertThat(savedStateHandle.get<Filters>(FilterViewModel.STATE_FILTER)).isEqualTo(
            //mapping Filters into FilterState
            fakeFilterState(types = emptyList(), emptyFilter)
        )

        //WHEN user selected only one type
        filterViewModel.updateTypeSelected(BuildingType.APARTMENT, true)

        //THEN state should have his type updated
        assertThat(savedStateHandle.get<Filters>(FilterViewModel.STATE_FILTER)).isEqualTo(
           fakeFilterState(listOf(BuildingType.APARTMENT), emptyFilter)
        )

    }

    @Test
    fun clearFilters_should_clear_all_filters()= runTest {
        //GIVEN
        filterViewModel.updateTypeSelected(BuildingType.APARTMENT, true)
        //WHEN
        filterViewModel.clearFilters()
        //THEN
        coVerify {
            //check if right filters are passed
            //mapping FilterState into Filters
            mockFilterRepository.setFilters(Filters(
                city = filterState.city,
                type = filterState.type,
                priceMin = filterState.priceMin,
                priceMax = filterState.priceMax,
                surfaceMin = filterState.surfaceMin,
                surfaceMax = filterState.surfaceMax,
                status = filterState.status
            ))
        }
    }

    @Test
    @Suppress("UnusedFlow")
    fun applyFilters_should_called_filterRepository() = runTest {
        //GIVEN
        //WHEN
        filterViewModel.applyFilters()
        //THEN
        coVerify {
            mockFilterRepository.getFilters()
        }
    }


}