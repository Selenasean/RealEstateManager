package com.openclassrooms.realestatemanager.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.data.Repository
import com.openclassrooms.realestatemanager.domain.RealEstateAgent
import kotlinx.coroutines.flow.map

class CreateViewModel(private val repository: Repository) : ViewModel() {


    //TODO : create object realEstate à envoyer en bdd

    //to get a list of agent's name
    fun getAgentsName(): LiveData<List<String>> {
        return repository.getAllAgents().map { agents ->
            agents.map {
                it.name
            }
        }.asLiveData()
    }

    private val _createdRealEstateMutableLiveDate = MutableLiveData<RealEstateCreatedState>()
    val state: LiveData<RealEstateCreatedState> = _createdRealEstateMutableLiveDate

    fun createRealEstate() {
        //TODO : create realestate in repo
    }


}

/**
 *
 */
data class RealEstateCreatedState(
    val type: String? = null,
    val address: String? = null,
    val city: String? = null,
    val price: String? = null,
    val surface: String? = null,
    val rooms: String? = null,
    val bedrooms: String? = null,
    val bathrooms: String? = null,
    val description: String?= null,
    val amenities: List<String> = emptyList(),
    val agentName: String? = null
){
    fun isCreatedEnabled(): Boolean{
         return listOf(
            type,
            address,
            city,
            price,
            surface,
            rooms,
            bedrooms,
            bathrooms,
            description,
            agentName
        ).none{ it.isNullOrBlank() }
    }
}
