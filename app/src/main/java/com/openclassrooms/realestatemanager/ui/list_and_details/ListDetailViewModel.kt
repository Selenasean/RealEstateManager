package com.openclassrooms.realestatemanager.ui.list_and_details


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.data.Repository
import com.openclassrooms.realestatemanager.domain.RealEstate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ListDetailViewModel(private val repository : Repository) : ViewModel() {

    private var mutableStateFlow = MutableStateFlow<Long?>(null)

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

    val listState: LiveData<List<ItemState>> = repository.getAllRealEstates().combine(mutableStateFlow) { listRealEstate, idSelected ->
             listRealEstate.map { realEstate ->
                 ItemState(
                     isSelected = realEstate.id == idSelected,
                     realEstate = realEstate
                 )
             }
        }.asLiveData()


}

data class ItemState(
    val isSelected: Boolean,
    val realEstate : RealEstate
)