package com.openclassrooms.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.model.RealEstateAgent
import kotlinx.coroutines.flow.Flow

@Dao
interface RealEstateAgentDao {
    @Insert
    fun createAgent(realEstateAgent: RealEstateAgent): Long

    @Query("SELECT * FROM realEstateAgents WHERE uid = :agentId")
    fun getOneAgent(agentId: Int): Flow<RealEstateAgent>

    @Query("SELECT * FROM realEstateAgents")
    fun getAllAgents(): Flow<List<RealEstateAgent>>

}