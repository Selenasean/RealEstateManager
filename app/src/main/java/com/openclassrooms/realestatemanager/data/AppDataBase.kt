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
import com.openclassrooms.realestatemanager.data.model.RealEstateDb
import com.openclassrooms.realestatemanager.data.model.RealEstateAgent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [Photo::class, RealEstateDb::class, RealEstateAgent::class],
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
                   val agentId1 = appDataBase.realEstateAgentDao().createAgent(RealEstateAgent(name = "Patrick"))
                   val agentId2 = appDataBase.realEstateAgentDao().createAgent(RealEstateAgent(name = "Didier"))
                   val agentId3 = appDataBase.realEstateAgentDao().createAgent(RealEstateAgent(name = "Corinne"))
                   appDataBase.realEstateDao().createRealEstate(
                       RealEstateDb(
                           type = BuildingType.HOUSE,
                           price = 23000F,
                           name = "Pistache",
                           realEstateAgentId = agentId1
                       )
                   )
                   appDataBase.realEstateDao().createRealEstate(
                       RealEstateDb(
                           type = BuildingType.VILA,
                           price = 380000F,
                           name = "Fruit de la Passion",
                           realEstateAgentId = agentId1
                       )
                   )
                   appDataBase.realEstateDao().createRealEstate(
                       RealEstateDb(
                           type = BuildingType.APARTMENT,
                           price = 150000F,
                           name = "Ananas",
                           realEstateAgentId = agentId2
                       )
                   )
                   appDataBase.realEstateDao().createRealEstate(
                       RealEstateDb(
                           type = BuildingType.HOUSE,
                           price = 350000F,
                           name = "Fraise",
                           realEstateAgentId = agentId2
                       )
                   )
                   appDataBase.realEstateDao().createRealEstate(
                       RealEstateDb(
                           type = BuildingType.VILA,
                           price = 580000F,
                           name = "Pomme",
                           realEstateAgentId = agentId3
                       )
                   )
                   appDataBase.realEstateDao().createRealEstate(
                       RealEstateDb(
                           type = BuildingType.APARTMENT,
                           price = 120000F,
                           name = "Abricot",
                           realEstateAgentId = agentId3
                       )
                   )
               }

            }
        }

    }

}


