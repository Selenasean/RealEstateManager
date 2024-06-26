package com.openclassrooms.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.openclassrooms.realestatemanager.data.model.Photo

@Dao
interface PhotoDao {

    @Insert
    fun createPhoto(photo: Photo)

    @Delete
    fun deletePhoto(photo: Photo)

    @Query("SELECT * FROM photos")
    fun getAllPhotos(): Flow<List<Photo>>
}