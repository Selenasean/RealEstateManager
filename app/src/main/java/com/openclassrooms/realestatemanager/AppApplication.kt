package com.openclassrooms.realestatemanager

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.data.AppDataBase
import com.openclassrooms.realestatemanager.data.AppDataBase.Companion.createAppDatabase
import com.openclassrooms.realestatemanager.data.GeocoderRepository
import com.openclassrooms.realestatemanager.data.Repository
import com.openclassrooms.realestatemanager.utils.internetConnectivity.ConnectivityChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AppApplication : Application() {


    val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob())
    lateinit var appDataBase: AppDataBase
    lateinit var repository: Repository
    lateinit var geocoderRepository: GeocoderRepository
    lateinit var connectivityChecker: ConnectivityChecker



    override fun onCreate() {
        super.onCreate()

        appDataBase = createAppDatabase(this, applicationScope, { appDataBase } )
        AppApplication.appContext = applicationContext
        connectivityChecker = ConnectivityChecker(applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        geocoderRepository = GeocoderRepository(AppApplication.appContext)
        repository = Repository(appDataBase, geocoderRepository)
    }

    companion object {
        lateinit  var appContext: Context
    }

}