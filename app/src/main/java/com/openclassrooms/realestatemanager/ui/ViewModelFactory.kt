package com.openclassrooms.realestatemanager.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.openclassrooms.realestatemanager.AppApplication
import com.openclassrooms.realestatemanager.ui.create.CreateViewModel
import com.openclassrooms.realestatemanager.ui.list_map_details.ListMapDetailViewModel


class ViewModelFactory : ViewModelProvider.Factory {


    companion object{
        private var factory: ViewModelFactory? = null

        fun getInstance() : ViewModelFactory {
            if(factory == null){
                synchronized(ViewModelFactory::class.java){
                    if(factory == null){
                        factory = ViewModelFactory()
                    }
                }
            }
            return factory!!
        }

    }


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application : AppApplication = extras[APPLICATION_KEY] as AppApplication
        val savedStateHandle : SavedStateHandle = extras.createSavedStateHandle()

        if(modelClass.isAssignableFrom(ListMapDetailViewModel::class.java)){
            return ListMapDetailViewModel(application.repository, application.geocoderRepository) as T
        }
        if(modelClass.isAssignableFrom(CreateViewModel::class.java)){
            return CreateViewModel(application.repository, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}