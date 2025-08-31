package com.openclassrooms.realestatemanager.data

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.openclassrooms.realestatemanager.data.model.Amenity
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.Status
import java.time.Instant

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): Instant? {
        return value?.let { Instant.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Instant?): String? {
        return date?.toString()
    }
    
    @TypeConverter
    fun listAmenitiesToJson(list : List<Amenity>?) = Gson().toJson(list)

    @TypeConverter
    fun JsonToListAmenities(json : String?) = Gson().fromJson(json, Array<Amenity>::class.java).toList()


}