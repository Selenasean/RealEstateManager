package com.openclassrooms.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanager.data.model.PhotoDb
import com.openclassrooms.realestatemanager.data.model.RealEstateDb
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


    //TODO: update state sold etcc

}