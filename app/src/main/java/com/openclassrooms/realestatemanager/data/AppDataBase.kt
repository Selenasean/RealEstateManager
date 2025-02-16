package com.openclassrooms.realestatemanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.realestatemanager.data.dao.PhotoDao
import com.openclassrooms.realestatemanager.data.dao.RealEstateAgentDao
import com.openclassrooms.realestatemanager.data.dao.RealEstateDao
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.Converters
import com.openclassrooms.realestatemanager.data.model.Photo
import com.openclassrooms.realestatemanager.data.model.RealEstateDb
import com.openclassrooms.realestatemanager.data.model.RealEstateAgent
import com.openclassrooms.realestatemanager.data.model.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Database(
    entities = [Photo::class, RealEstateDb::class, RealEstateAgent::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
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
                           surface = 170,
                           rooms = 3,
                           description = "Superbe maison dans le bourg de Serris, 3 chambres, cuisine toute équipée, salle de bain moderne. Dispose d'un garage d'une superficie de 50m². S'étale sur 2 étages, un toilette par étage. Parfait pour jeune couple !",
                           address = "22 rue Emile Cloud",
                           city = "Serris",
                           nearbyBusiness = "Boulangerie, écoles",
                           status = Status.FOR_SALE,
                           dateCreated = LocalDateTime.of(LocalDate.of(2024,7,30), LocalTime.of(14,0)),
                           dateOfSale = null,
                           realEstateAgentId = agentId1
                       )
                   )
                   appDataBase.realEstateDao().createRealEstate(
                       RealEstateDb(
                           type = BuildingType.VILA,
                           price = 380000F,
                           name = "Fruit de la Passion",
                           surface = 300,
                           rooms = 8,
                           description = "Superbe vila dans le bourg de Versailles, 8 chambres, cuisine toute équipée, 2 salles de bain modernes. Dispose d'un garage d'une superficie de 50m². S'étale sur 2 étages, un toilette par étage.",
                           address = "2 avenue de Paris",
                           city = "Versailles",
                           nearbyBusiness = "centre commercial, écoles, musés",
                           status = Status.FOR_SALE,
                           dateCreated = LocalDateTime.of(LocalDate.of(2024,7,30), LocalTime.of(14,0)),
                           dateOfSale = null,
                           realEstateAgentId = agentId1
                       )
                   )
                   appDataBase.realEstateDao().createRealEstate(
                       RealEstateDb(
                           type = BuildingType.APARTMENT,
                           price = 150000F,
                           name = "Ananas",
                           surface = 80,
                           rooms = 3,
                           description = "Superbe appartement au centre de Créteil, 3 chambres, cuisine toute équipée, salle de bain moderne. Au 2e étage d'un bâtiment des années 80, avec ascenseur. Parfait pour jeune couple !",
                           address = "24 avenue du Marechal de Lattre de Tassigny",
                           city = "Créteil",
                           nearbyBusiness = "Metro, centre commercial, écoles",
                           status = Status.FOR_SALE,
                           dateCreated = LocalDateTime.of(LocalDate.of(2024,7,30), LocalTime.of(14,0)),
                           dateOfSale = null,
                           realEstateAgentId = agentId2
                       )
                   )
                   appDataBase.realEstateDao().createRealEstate(
                       RealEstateDb(
                           type = BuildingType.HOUSE,
                           price = 350000F,
                           name = "Fraise",
                           surface = 100,
                           rooms = 4,
                           description = "Superbe maison dans le bourg d'Orly, 4 chambres, cuisine toute équipée, salle de bain moderne. Dispose d'un garage d'une superficie de 50m². S'étale sur 2 étages, un toilette par étage. Parfait pour jeune couple !",
                           address = "3bis rue Roger Salengro",
                           city = "Orly",
                           nearbyBusiness = "Boulangerie, écoles, aeroport",
                           status = Status.FOR_SALE,
                           dateCreated = LocalDateTime.of(LocalDate.of(2024,7,30), LocalTime.of(14,0)),
                           dateOfSale = null,
                           realEstateAgentId = agentId2
                       )
                   )
                   appDataBase.realEstateDao().createRealEstate(
                       RealEstateDb(
                           type = BuildingType.VILA,
                           price = 580000F,
                           name = "Pomme",
                           surface = 450,
                           rooms = 11,
                           description = "Superbe vila dans le bourg de Montlignon, 11 chambres, cuisine toute équipée, 3 salles de bain modernes. Dispose d'un garage d'une superficie de 80m². S'étale sur 2 étages, un toilette par étage.",
                           address = "18 allée de la Chasse",
                           city = "Montlignon",
                           nearbyBusiness = "Boulangerie, écoles, forêt et parc",
                           status = Status.FOR_SALE,
                           dateCreated = LocalDateTime.of(LocalDate.of(2024,7,30), LocalTime.of(14,0)),
                           dateOfSale = null,
                           realEstateAgentId = agentId3
                       )
                   )
                   appDataBase.realEstateDao().createRealEstate(
                       RealEstateDb(
                           type = BuildingType.APARTMENT,
                           price = 120000F,
                           name = "Abricot",
                           surface = 50,
                           rooms = 2,
                           description = "Superbe appartement dans le centre de Montreuil, 2 chambres, cuisine toute équipée, salle de bain moderne. Au 4e étage avec ascensseur. Parfait pour jeune couple !",
                           address = "9 rue Parmentier",
                           city = "Montreuil",
                           nearbyBusiness = null,
                           status = Status.FOR_SALE,
                           dateCreated = LocalDateTime.of(LocalDate.of(2024,7,30), LocalTime.of(14,0)),
                           dateOfSale = null,
                           realEstateAgentId = agentId3
                       )
                   )
               }

            }
        }

    }

}


