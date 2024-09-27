package com.openclassrooms.realestatemanager

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.data.AppDataBase
import com.openclassrooms.realestatemanager.data.AppDataBase.Companion.createAppDatabase
import com.openclassrooms.realestatemanager.data.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AppApplication : Application() {


    val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob())
    lateinit var appDataBase: AppDataBase
    lateinit var repository: Repository



    override fun onCreate() {
        super.onCreate()

        appDataBase = createAppDatabase(this, applicationScope, { appDataBase } )
        repository = Repository(appDataBase)
        AppApplication.appContext = applicationContext

    }

    companion object {
        lateinit  var appContext: Context
    }

}