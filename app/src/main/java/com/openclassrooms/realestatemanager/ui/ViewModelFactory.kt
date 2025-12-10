package com.openclassrooms.realestatemanager.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.openclassrooms.realestatemanager.AppApplication
import com.openclassrooms.realestatemanager.data.FilterRepository
import com.openclassrooms.realestatemanager.domain.notifications.NotificationHelper
import com.openclassrooms.realestatemanager.ui.create_edit.CreateEditViewModel
import com.openclassrooms.realestatemanager.ui.filter.FilterViewModel
import com.openclassrooms.realestatemanager.ui.list_map_details.ListMapDetailViewModel
import com.openclassrooms.realestatemanager.utils.internetConnectivity.ConnectivityChecker

/**
 * ViewModelFactory
 * To create and initialize ViewModels with custom parameters
 * To enhance code organisation and testability
 * To promoting separation of concerns and improve dependence injection
 */

class ViewModelFactory : ViewModelProvider.Factory {

    companion object {
        private var factory: ViewModelFactory? = null

        fun getInstance(): ViewModelFactory {
            if (factory == null) {
                synchronized(ViewModelFactory::class.java) {
                    if (factory == null) {
                        factory = ViewModelFactory()
                    }
                }
            }
            return factory!!
        }

    }


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application: AppApplication = extras[APPLICATION_KEY] as AppApplication
        val savedStateHandle: SavedStateHandle = extras.createSavedStateHandle()
        val connectivityChecker: ConnectivityChecker = application.connectivityChecker
        val notificationHelper: NotificationHelper = application.notificationHelper


        if (modelClass.isAssignableFrom(ListMapDetailViewModel::class.java)) {
            return ListMapDetailViewModel(
                application.repository,
                savedStateHandle,
                application.locationRepository,
                application.filterRepository
            ) as T
        }
        if (modelClass.isAssignableFrom(CreateEditViewModel::class.java)) {
            val realEstateId: String? = savedStateHandle["REAL_ESTATE_ID"]
            return CreateEditViewModel(
                realEstateId,
                application.repository,
                savedStateHandle,
                connectivityChecker,
                notificationHelper
            ) as T
        }
        if (modelClass.isAssignableFrom(FilterViewModel::class.java)) {
            return FilterViewModel(
                savedStateHandle,
                application.filterRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}