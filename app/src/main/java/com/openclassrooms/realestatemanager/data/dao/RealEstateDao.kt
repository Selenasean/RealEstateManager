package com.openclassrooms.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanager.data.model.RealEstateDb
import kotlinx.coroutines.flow.Flow

@Dao
interface RealEstateDao {

    @Insert
    fun createRealEstate(realEstate: RealEstateDb) : Long

    @Delete
    fun deleteRealEstate(realEstate: RealEstateDb)

    @Update
    fun updateRealEstate(realEstate: RealEstateDb)

    //TODO: update state sold etcc

    @Query("SELECT * FROM realEstates")
    fun getAllRealEstates(): Flow<List<RealEstateDb>>

    @Query("SELECT * FROM realEstates WHERE uid = :realEstateId")
    fun getOneRealEstate(realEstateId: Long): Flow<RealEstateDb>

}