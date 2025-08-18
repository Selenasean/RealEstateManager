package com.openclassrooms.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.PhotoDb
import com.openclassrooms.realestatemanager.data.model.RealEstateDb
import com.openclassrooms.realestatemanager.data.model.Status
import kotlinx.coroutines.flow.Flow

@Dao
interface RealEstateDao {

    @Insert
    suspend fun createRealEstate(realEstate: RealEstateDb) : Long

    @Delete
    suspend fun deleteRealEstate(realEstate: RealEstateDb)

    @Update
     suspend fun updateRealEstate(realEstate: RealEstateDb)

    @Query("SELECT * FROM realEstates LEFT JOIN photos ON realEstates.id = photos.realEstateId")
    fun getAllRealEstates(): Flow<Map<RealEstateDb, List<PhotoDb>>>

    @Query("SELECT * FROM realEstates LEFT JOIN photos ON realEstates.id = photos.realEstateId WHERE realEstates.id = :realEstateId ")
    fun getOneRealEstate(realEstateId: Long): Flow<Map<RealEstateDb, List<PhotoDb>>>

    @Query("SELECT * FROM realEstates LEFT JOIN photos ON realEstates.id = photos.realEstateId WHERE realEstates.id = :realEstateId ")
    suspend fun fetchOneRealEstate(realEstateId: Long): Map<RealEstateDb, List<PhotoDb>>

    @Query("SELECT * FROM realEstates LEFT JOIN photos ON realEstates.id = photos.realEstateId WHERE " +
            "(:city IS NULL OR city LIKE '%' || :city || '%' ) AND " +
//            "(:type IS NULL OR type IN (:type)) AND " +
            "(:minPrice IS NULL OR price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR price <= :maxPrice) AND " +
            "(:minSurface IS NULL OR surface >= :minSurface) AND " +
            "(:maxSurface IS NULL OR surface <= :maxSurface) AND " +
            "(:status IS NULL OR status = :status)")
    fun getRealEstatesFiltered(
        city: String?,
//        type: List<BuildingType>,
        minPrice: Int?,
        maxPrice: Int?,
        minSurface: Int?,
        maxSurface: Int?,
        status : Status?
    ): Flow<Map<RealEstateDb, List<PhotoDb>>>

}