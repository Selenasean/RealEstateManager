package com.openclassrooms.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanager.data.model.RealEstateAgentDb
import kotlinx.coroutines.flow.Flow

@Dao
interface RealEstateAgentDao {
    @Insert
    fun createAgent(realEstateAgent: RealEstateAgentDb): Long

    @Query("SELECT * FROM realEstateAgents WHERE id = :agentId")
    fun getOneAgent(agentId: Int): Flow<RealEstateAgentDb>

    @Query("SELECT * FROM realEstateAgents WHERE id = :agentId")
    suspend fun fetchOneAgent(agentId: Long): RealEstateAgentDb

    @Query("SELECT * FROM realEstateAgents")
    fun getAllAgents(): Flow<List<RealEstateAgentDb>>

    @Query("SELECT * FROM realEstateAgents")
    suspend fun fetchAllAgents(): List<RealEstateAgentDb>

}