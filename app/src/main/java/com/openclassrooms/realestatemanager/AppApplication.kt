package com.openclassrooms.realestatemanager

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.openclassrooms.realestatemanager.data.AppDataBase
import com.openclassrooms.realestatemanager.data.AppDataBase.Companion.createAppDatabase
import com.openclassrooms.realestatemanager.data.GeocoderRepository
import com.openclassrooms.realestatemanager.data.Repository
import com.openclassrooms.realestatemanager.data.location.LocationRepository
import com.openclassrooms.realestatemanager.utils.internetConnectivity.ConnectivityChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AppApplication : Application() {


    val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob())
    lateinit var appDataBase: AppDataBase
    lateinit var repository: Repository
    lateinit var locationRepository: LocationRepository
    lateinit var geocoderRepository: GeocoderRepository
    lateinit var connectivityChecker: ConnectivityChecker
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun onCreate() {
        super.onCreate()

        appDataBase = createAppDatabase(this, applicationScope, { appDataBase })
        AppApplication.appContext = applicationContext
        connectivityChecker =
            ConnectivityChecker(applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        geocoderRepository = GeocoderRepository(AppApplication.appContext)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)
        repository = Repository(appDataBase, geocoderRepository)
        locationRepository = LocationRepository(fusedLocationProviderClient)
    }

    companion object {
        lateinit var appContext: Context
    }

}