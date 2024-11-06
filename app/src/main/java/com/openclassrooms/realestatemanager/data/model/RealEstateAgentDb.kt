package com.openclassrooms.realestatemanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "realEstateAgents")
data class RealEstateAgentDb(

    @PrimaryKey(autoGenerate = true) val uid: Long = 0,

    val name: String,
)
