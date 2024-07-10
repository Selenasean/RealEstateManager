package com.openclassrooms.realestatemanager.ui.list_and_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.data.Repository
import com.openclassrooms.realestatemanager.data.model.RealEstateDb
import com.openclassrooms.realestatemanager.domain.RealEstate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.switchMap

class ListDetailViewModel(private val repository : Repository) : ViewModel() {

    var mutableStateFlow = MutableStateFlow<Long?>(null)
    @OptIn(ExperimentalCoroutinesApi::class)
    val detailState : LiveData<RealEstate?> = mutableStateFlow.flatMapLatest { id ->
        if(id != null){
            repository.getOneRealEstates(id)
        }else{
            flowOf(null)
        }
    }.asLiveData()


    fun onRealEstateClick(id: Long) {
        mutableStateFlow.value = id
    }

    val listState: LiveData<List<RealEstate>> = repository.getAllRealEstates().asLiveData()




}