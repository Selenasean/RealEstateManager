package com.openclassrooms.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanager.data.model.RealEstate
import kotlinx.coroutines.flow.Flow

@Dao
interface RealEstateDao {

    @Insert
    fun createRealEstate(realEstate: RealEstate)

    @Delete
    fun deleteRealEstate(realEstate: RealEstate)

    @Update
    fun updateRealEstate(realEstate: RealEstate)

    //TODO: update state sold etcc

    @Query("SELECT * FROM realEstates")
    fun getAllRealEstates(): Flow<List<RealEstate>>

    @Query("SELECT * FROM realEstates WHERE uid = :realEstateId")
    fun getOneRealEstate(realEstateId: Int): Flow<RealEstate>

}