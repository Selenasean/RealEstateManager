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
import com.openclassrooms.realestatemanager.data.model.Amenity
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.PhotoDb
import com.openclassrooms.realestatemanager.data.model.RealEstateDb
import com.openclassrooms.realestatemanager.data.model.RealEstateAgentDb
import com.openclassrooms.realestatemanager.data.model.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

@Database(
    entities = [PhotoDb::class, RealEstateDb::class, RealEstateAgentDb::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {

    //DAO
    abstract fun photoDao(): PhotoDao
    abstract fun realEstateDao(): RealEstateDao
    abstract fun realEstateAgentDao(): RealEstateAgentDao


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
                   //fake agent
                   val agentId1 = appDataBase.realEstateAgentDao().createAgent(RealEstateAgentDb(name = "Patrick"))
                   val agentId2 = appDataBase.realEstateAgentDao().createAgent(RealEstateAgentDb(name = "Didier"))
                   val agentId3 = appDataBase.realEstateAgentDao().createAgent(RealEstateAgentDb(name = "Corinne"))
                   //fake estate and associated photos
                   val estateId1 = appDataBase.realEstateDao().createRealEstate(
                       RealEstateDb(
                           type = BuildingType.HOUSE,
                           price = 23000F,
                           name = "Pistache",
                           surface = 170,
                           rooms = 5,
                           bathrooms = 1,
                           bedrooms = 3,
                           description = "Superbe maison dans le bourg de Serris, 3 chambres, cuisine toute équipée, salle de bain moderne. Dispose d'un garage d'une superficie de 50m². S'étale sur 2 étages, un toilette par étage. Parfait pour jeune couple !",
                           address = "22 rue Emile Cloud",
                           city = "Serris",
                           status = Status.FOR_SALE,
                           amenities = listOf(Amenity.BAKERY, Amenity.SCHOOL),
                           dateCreated = LocalDateTime.of(LocalDate.of(2024,7,30), LocalTime.of(14,0)).toInstant(
                               ZoneOffset.UTC),
                           dateOfSale = null,
                           realEstateAgentId = agentId1
                       )
                   )
                   appDataBase.photoDao().createPhoto(
                       PhotoDb(
                           realEstateId = estateId1,
                           urlPhoto = "https://images.unsp" +
                                   "lash.com/photo-1504615755583-2916b52192a3?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OHx8aG91c2V8ZW58MHx8MHx8fDA%3D"
                       )
                   )
                   appDataBase.photoDao().createPhoto(
                       PhotoDb(
                           realEstateId = estateId1,
                           urlPhoto = "https://plus.unsplash.com/premium_photo-1661778773089-8718bcedb39e?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
                       )
                   )
                   appDataBase.photoDao().createPhoto(
                       PhotoDb(
                           realEstateId = estateId1,
                           urlPhoto = "https://images.unsplash.com/photo-1514053026555-49ce8886ae41?q=80&w=1887&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
                       )
                   )
                   appDataBase.photoDao().createPhoto(
                       PhotoDb(
                           realEstateId = estateId1,
                           urlPhoto = "https://plus.unsplash.com/premium_photo-1683888725059-b912dda58ba0?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
                       )
                   )
                   appDataBase.realEstateDao().createRealEstate(
                       RealEstateDb(
                           type = BuildingType.VILA,
                           price = 380000F,
                           name = "Fruit de la Passion",
                           surface = 300,
                           rooms = 10,
                           bathrooms = 2,
                           bedrooms = 8,
                           description = "Superbe vila dans le bourg de Versailles, 8 chambres, cuisine toute équipée, 2 salles de bain modernes. Dispose d'un garage d'une superficie de 50m². S'étale sur 2 étages, un toilette par étage.",
                           address = "2 avenue de Paris",
                           city = "Versailles",
                           status = Status.FOR_SALE,
                           amenities = listOf(Amenity.BAKERY, Amenity.SCHOOL, Amenity.SHOPPING_MALL),
                           dateCreated = LocalDateTime.of(LocalDate.of(2024,7,30), LocalTime.of(14,0)).toInstant(
                               ZoneOffset.UTC),
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
                           rooms = 5,
                           bathrooms = 1,
                           bedrooms = 3,
                           description = "Superbe appartement au centre de Créteil, 3 chambres, cuisine toute équipée, salle de bain moderne. Au 2e étage d'un bâtiment des années 80, avec ascenseur. Parfait pour jeune couple !",
                           address = "24 avenue du Marechal de Lattre de Tassigny",
                           city = "Créteil",
                           status = Status.FOR_SALE,
                           amenities = listOf(Amenity.BAKERY, Amenity.SCHOOL, Amenity.SHOPPING_MALL, Amenity.STATION, Amenity.GYM),
                           dateCreated =LocalDateTime.of(LocalDate.of(2024,7,30), LocalTime.of(14,0)).toInstant(
                               ZoneOffset.UTC),
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
                           rooms = 6,
                           bathrooms = 1,
                           bedrooms = 4,
                           description = "Superbe maison dans le bourg d'Orly, 4 chambres, cuisine toute équipée, salle de bain moderne. Dispose d'un garage d'une superficie de 50m². S'étale sur 2 étages, un toilette par étage. Parfait pour jeune couple !",
                           address = "3bis rue Roger Salengro",
                           city = "Orly",
                           status = Status.FOR_SALE,
                           amenities = listOf(Amenity.BAKERY, Amenity.SCHOOL),
                           dateCreated = LocalDateTime.of(LocalDate.of(2024,7,30), LocalTime.of(14,0)).toInstant(
                               ZoneOffset.UTC),
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
                           rooms = 13,
                           bathrooms = 3,
                           bedrooms = 11,
                           description = "Superbe vila dans le bourg de Montlignon, 11 chambres, cuisine toute équipée, 3 salles de bain modernes. Dispose d'un garage d'une superficie de 80m². S'étale sur 2 étages, un toilette par étage.",
                           address = "18 allée de la Chasse",
                           city = "Montlignon",
                           status = Status.SOLD,
                           amenities = listOf(Amenity.BAKERY, Amenity.SCHOOL),
                           dateCreated = LocalDateTime.of(LocalDate.of(2024,7,30), LocalTime.of(14,0)).toInstant(
                               ZoneOffset.UTC),
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
                           rooms = 4,
                           bathrooms = 1,
                           bedrooms = 2,
                           description = "Superbe appartement dans le centre de Montreuil, 2 chambres, cuisine toute équipée, salle de bain moderne. Au 4e étage avec ascensseur. Parfait pour jeune couple !",
                           address = "9 rue Parmentier",
                           city = "Montreuil",
                           status = Status.FOR_SALE,
                           amenities = listOf(Amenity.BAKERY, Amenity.SCHOOL),
                           dateCreated = LocalDateTime.of(LocalDate.of(2024,7,30), LocalTime.of(14,0)).toInstant(
                               ZoneOffset.UTC),
                           dateOfSale = null,
                           realEstateAgentId = agentId3
                       )
                   )
               }

            }
        }

    }

}


