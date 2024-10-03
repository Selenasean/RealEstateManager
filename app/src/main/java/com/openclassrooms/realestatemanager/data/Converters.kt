package com.openclassrooms.realestatemanager.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.openclassrooms.realestatemanager.data.model.Amenity
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
    fun listToJson(list : List<Amenity>?) = Gson().toJson(list)

    @TypeConverter
    fun JsonToList(json : String) = Gson().fromJson(json, Array<Amenity>::class.java).toList()


}