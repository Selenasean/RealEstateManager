package com.openclassrooms.realestatemanager.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.domain.Filters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FilterRepository(private val dataStore: DataStore<Preferences>) {


    fun getFilters(): Flow<Filters> {
        return dataStore.data.map { preferences ->
            Filters(
                city = preferences[Keys.CITY],
                type = preferences[Keys.BUILDING_TYPE]?.split(",")?.mapNotNull { stringType ->
                    BuildingType.entries.firstOrNull { type -> type.name == stringType }
                } ?: emptyList(),
                priceMax = preferences[Keys.PRICE_MAX],
                priceMin = preferences[Keys.PRICE_MIN],
                surfaceMin = preferences[Keys.SURFACE_MAX],
                surfaceMax = preferences[Keys.SURFACE_MIN],
                status = Status.entries.firstOrNull { status ->
                    status.name == preferences[Keys.STATUS]
                }
            )

        }
    }

    suspend fun setFilters(filters: Filters) {
        dataStore.edit { preferences ->
            if(filters.city != null) preferences[Keys.CITY] = filters.city else preferences.remove(Keys.CITY)
            if(filters.priceMax != null) preferences[Keys.PRICE_MAX] = filters.priceMax else preferences.remove(Keys.PRICE_MAX)
            if(filters.type.isEmpty()){
                preferences.remove(Keys.BUILDING_TYPE)
            }  else {
                preferences[Keys.BUILDING_TYPE] = filters.type.joinToString(",") { type ->
                    type.name
                }
            }
            if(filters.surfaceMax != null) preferences[Keys.SURFACE_MAX] = filters.surfaceMax else preferences.remove(Keys.SURFACE_MAX)
            if(filters.surfaceMin != null) preferences[Keys.SURFACE_MIN] = filters.surfaceMin else preferences.remove(Keys.SURFACE_MIN)
            if(filters.status != null) preferences[Keys.STATUS] = filters.status.name else preferences.remove(Keys.STATUS)
        }
    }


}

private object Keys {
    val CITY = stringPreferencesKey("city")
    val PRICE_MAX = intPreferencesKey("price_max")
    val PRICE_MIN = intPreferencesKey("price_min")
    val BUILDING_TYPE = stringPreferencesKey("BuildingType")
    val SURFACE_MAX = intPreferencesKey("surface_max")
    val SURFACE_MIN = intPreferencesKey("surface_min")
    val STATUS = stringPreferencesKey("status")
}

