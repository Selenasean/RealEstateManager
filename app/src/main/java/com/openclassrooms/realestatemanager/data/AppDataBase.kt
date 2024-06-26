package com.openclassrooms.realestatemanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.realestatemanager.data.dao.PhotoDao
import com.openclassrooms.realestatemanager.data.dao.RealEstateAgentDao
import com.openclassrooms.realestatemanager.data.dao.RealEstateDao
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.Photo
import com.openclassrooms.realestatemanager.data.model.RealEstate
import com.openclassrooms.realestatemanager.data.model.RealEstateAgent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [Photo::class, RealEstate::class, RealEstateAgent::class],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    //DAO
    abstract fun photoDao(): PhotoDao
    abstract fun realEstateDao(): RealEstateDao
    abstract fun realEstateAgentDao(): RealEstateAgentDao

    //TODO: prepopulate & add callback

    companion object {
        fun createAppDatabase(context: Context, applicationScope: CoroutineScope, databaseProvider:() -> AppDataBase): AppDataBase {
            val database: AppDataBase = Room.databaseBuilder(
                context,
                AppDataBase::class.java,
                "RealEstateDatabase.db"
            )
                .addCallback(PrepopulateDatabase(applicationScope, databaseProvider))
                .build()
            return database
        }
    }

    private class PrepopulateDatabase(
        private val applicationScope: CoroutineScope,
        private val databaseProvider:() -> AppDataBase
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val appDataBase = databaseProvider()
            applicationScope.launch {
               appDataBase.withTransaction {
                   val agentId =
                       appDataBase.realEstateAgentDao().createAgent(RealEstateAgent(name = "Patrick"))
                   appDataBase.realEstateDao().createRealEstate(
                       RealEstate(
                           type = BuildingType.HOUSE,
                           price = 23000F,
                           realEstateAgentId = agentId
                       )
                   )
               }

            }
        }

    }

}