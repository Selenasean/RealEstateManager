package com.openclassrooms.realestatemanager

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.data.AppDataBase
import com.openclassrooms.realestatemanager.data.AppDataBase.Companion.createAppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AppApplication : Application() {


    val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob())
    lateinit var appDataBase: AppDataBase

    override fun onCreate() {
        super.onCreate()

        appDataBase = createAppDatabase(this, applicationScope, { appDataBase } )
    }
}