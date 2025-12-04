package com.openclassrooms.realestatemanager

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceDataStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.openclassrooms.realestatemanager.data.AppDataBase
import com.openclassrooms.realestatemanager.data.AppDataBase.Companion.createAppDatabase
import com.openclassrooms.realestatemanager.data.FilterRepository
import com.openclassrooms.realestatemanager.data.GeocoderRepository
import com.openclassrooms.realestatemanager.data.Repository
import com.openclassrooms.realestatemanager.data.location.LocationRepository
import com.openclassrooms.realestatemanager.utils.internetConnectivity.ConnectivityChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.util.prefs.Preferences

class AppApplication : Application() {


    val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob())
    lateinit var appDataBase: AppDataBase
    lateinit var repository: Repository
    lateinit var filterRepository: FilterRepository
    lateinit var locationRepository: LocationRepository
    lateinit var geocoderRepository: GeocoderRepository
    lateinit var connectivityChecker: ConnectivityChecker
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val dataStore by preferencesDataStore(name = "datastore")


    override fun onCreate() {
        super.onCreate()

        appDataBase = createAppDatabase(this, applicationScope, { appDataBase })
        appContext = applicationContext

        connectivityChecker =
            ConnectivityChecker(applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        //localisation
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)

        //repositories
        geocoderRepository = GeocoderRepository(appContext)
        repository = Repository(appDataBase, geocoderRepository)
        locationRepository = LocationRepository(fusedLocationProviderClient)
        filterRepository = FilterRepository(dataStore)



    }



    companion object {
        lateinit var appContext: Context
    }

}