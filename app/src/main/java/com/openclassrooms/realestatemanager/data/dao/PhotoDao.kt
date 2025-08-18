package com.openclassrooms.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.openclassrooms.realestatemanager.data.model.PhotoDb

@Dao
interface PhotoDao {

    @Insert
    suspend fun createPhoto(photo: PhotoDb)

    @Delete
    suspend fun deletePhoto(photo: PhotoDb)

    @Query("DELETE FROM photos WHERE id= :photoId")
    suspend fun deleteFromId(photoId: String)

    @Query("SELECT * FROM photos")
    fun getAllPhotos(): Flow<List<PhotoDb>>

    @Update
    suspend fun updatePhoto(photo: PhotoDb)
}