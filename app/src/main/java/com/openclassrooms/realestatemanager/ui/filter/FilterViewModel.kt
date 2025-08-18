package com.openclassrooms.realestatemanager.ui.filter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.FilterRepository
import com.openclassrooms.realestatemanager.data.Repository
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.domain.Filters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FilterViewModel(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
    private val filterRepository: FilterRepository
) : ViewModel() {

    //TODO : communicate datastore content to other ListMapDetailVM

    companion object {
        val STATE_FILTER = "STATE_FILTER"
    }


    init {
        Log.i("init", "state: ${savedStateHandle.get<FilterState>(STATE_FILTER)} ")
        if (!savedStateHandle.contains(STATE_FILTER)) {
            //first creation of the VM
            viewModelScope.launch {
                savedStateHandle[STATE_FILTER] = filterRepository.getFilters().first().toFilterState()
                Log.i("init", "ça passe ")
            }
        }
    }


    //state of data in bundle
    private val _state: MutableStateFlow<FilterState> = savedStateHandle.getMutableStateFlow(STATE_FILTER,
        FilterState())
    val state: LiveData<FilterState> = _state.asLiveData()

    /**
     * To apply filters selected by user
     */
   fun applyFilters(){
        viewModelScope.launch {
            filterRepository.setFilters(_state.value.toFilters())
        }
    }


    fun clearFilters(){
        viewModelScope.launch {
            filterRepository.setFilters(FilterState().toFilters())
        }
    }

    fun updateCity(city: String) {
        val currentState = _state.value
        _state.value = currentState.copy(city = city)
        Log.i("filter VM", "updateCity: ${_state.value}")
    }

    fun updatePriceMin(priceMin: String) {
        val currentState = _state.value
        if (priceMin.isEmpty()) _state.value =
            currentState.copy(priceMin = null)
        else _state.value =
            currentState.copy(priceMin = priceMin.toInt())
        Log.i("filter VM", "updatePriceMin: ${_state.value}")
    }

    fun updatePriceMax(priceMax: String) {
        val currentState = _state.value
        if (priceMax.isEmpty()) _state.value =
            currentState.copy(priceMax = null)
        else _state.value =
            currentState.copy(priceMax = priceMax.toInt())
        Log.i("filter VM", "updatePriceMax: ${_state.value}")
    }

    fun updateSurfaceMin(surfaceMin: String) {
        val currentState = _state.value
        if (surfaceMin.isEmpty()) _state.value =
            currentState.copy(surfaceMin = null)
        else _state.value =
            currentState.copy(surfaceMin = surfaceMin.toInt())
        Log.i("filter VM", "updateSurfaceMin: ${_state.value}")
    }

    fun updateSurfaceMax(surfaceMax: String) {
        val currentState = _state.value
        if (surfaceMax.isEmpty()) _state.value =
            currentState.copy(surfaceMax = null)
        else _state.value =
            currentState.copy(surfaceMax =surfaceMax.toInt())
        Log.i("filter VM", "updateSurfaceMax: ${_state.value}")
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
        Log.i("filter VM", "updateTypeSelected: ${_state.value}")
    }

    fun updateStatus(status: Status) {
        val currentState = _state.value
        _state.value = currentState.copy(status = status)
        Log.i("filter VM", "status: ${_state.value}")
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