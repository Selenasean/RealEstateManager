package com.openclassrooms.realestatemanager.ui.filter


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.FilterRepository
import com.openclassrooms.realestatemanager.data.Repository
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.domain.Filters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FilterViewModel(
    savedStateHandle: SavedStateHandle,
    private val filterRepository: FilterRepository
) : ViewModel() {


    companion object {
        val STATE_FILTER = "STATE_FILTER"
    }


    init {
        if (!(savedStateHandle.contains(STATE_FILTER))) {
            //first creation of the VM
            viewModelScope.launch {
                savedStateHandle[STATE_FILTER] = filterRepository.getFilters().first().toFilterState()
            }

        }
    }


    //state of data in bundle
    private val _state: MutableStateFlow<FilterState> = savedStateHandle.getMutableStateFlow(STATE_FILTER,
        FilterState())
    val state: StateFlow<FilterState> = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FilterState()
    )

    /**
     * To apply filters selected by user
     */
   fun applyFilters(){

        viewModelScope.launch {
            filterRepository.setFilters(_state.value.toFilters())

        }
    }

    /**
     * To clear all filters
     */
    fun clearFilters(){
        viewModelScope.launch {
            filterRepository.setFilters(FilterState().toFilters())
        }
    }

    fun updateCity(city: String) {
        val currentState = _state.value
        _state.value = currentState.copy(city = city)
       }

    fun updatePriceMin(priceMin: String) {
        val currentState = _state.value
        if (priceMin.isEmpty()) _state.value =
            currentState.copy(priceMin = null)
        else _state.value =
            currentState.copy(priceMin = priceMin.toInt())
       }

    fun updatePriceMax(priceMax: String) {
        val currentState = _state.value
        if (priceMax.isEmpty()) _state.value =
            currentState.copy(priceMax = null)
        else _state.value =
            currentState.copy(priceMax = priceMax.toInt())
      }

    fun updateSurfaceMin(surfaceMin: String) {
        val currentState = _state.value
        if (surfaceMin.isEmpty()) _state.value =
            currentState.copy(surfaceMin = null)
        else _state.value =
            currentState.copy(surfaceMin = surfaceMin.toInt())
        }

    fun updateSurfaceMax(surfaceMax: String) {
        val currentState = _state.value
        if (surfaceMax.isEmpty()) _state.value =
            currentState.copy(surfaceMax = null)
        else _state.value =
            currentState.copy(surfaceMax = surfaceMax.toInt())
        }

    fun updateTypeSelected(type: BuildingType, isChosen: Boolean) {
        val currentState = _state.value
        val listType: MutableList<BuildingType> = currentState.type.toMutableList()
        if (isChosen) {
            listType.add(type)
        } else {
            listType.remove(type)
        }
        _state.value = currentState.copy(type = listType)

    }

    fun updateStatus(status: Status?) {
        val currentState = _state.value
        _state.value = currentState.copy(status = status)
        }

    //MAPPING FUNCTION
    fun FilterState.toFilters() : Filters{
        return Filters(
            city = this.city,
            type = this.type,
            priceMin = this.priceMin,
            priceMax = this.priceMax,
            surfaceMin = this.surfaceMin,
            surfaceMax = this.surfaceMax,
            status = this.status,
        )
    }

    fun Filters.toFilterState(): FilterState{
        return FilterState(
            city = this.city,
            type = this.type,
            priceMin = this.priceMin,
            priceMax = this.priceMax,
            surfaceMin = this.surfaceMin,
            surfaceMax = this.surfaceMax,
            status = this.status,
        )
    }
}