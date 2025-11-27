package com.openclassrooms.realestatemanager.provider

import android.content.ContentUris
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.core.net.toUri
import com.openclassrooms.realestatemanager.data.Repository

/**
 * This class contains the logic for the ContentProvider
 * It's used to test the logic
 */
class HandlerContentProvider(
    private val repository: Repository
) {

    companion object {
        //authority for the content provider : package name
        private const val AUTHORITY = "com.openclassrooms.realestatemanager.provider"

        //the bas URI that will be use to access the provider
        val CONTENT_URI: Uri = "content://$AUTHORITY/real_estate".toUri()

        //URI Matcher codes
        private const val REAL_ESTATES = 1
        private const val REAL_ESTATE_ID = 2

        //URI Matcher to make the difference between request for all items and only for one
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "real_estate", REAL_ESTATES)
            addURI(AUTHORITY, "real_estate/#", REAL_ESTATE_ID)
        }
    }

    /**
     * method that handles query request
     * @return Cursor
     */
    fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor {
        val cursor: Cursor = when (uriMatcher.match(uri)) {
            REAL_ESTATES -> repository.getAllRealEstatesWithCursor()
            REAL_ESTATE_ID -> {
                val id = ContentUris.parseId(uri)
                repository.getOneRealEstateWithCursor(id)
            }
            else -> throw IllegalArgumentException("Unknown URI : $uri")
        }
        return cursor
    }

    /**
     * Returns the MIME type for the data at the given URI
     */
    fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            REAL_ESTATES -> "vnd.android.cursor.dir/$AUTHORITY.real_estate"
            REAL_ESTATE_ID -> "vnd.android.cursor.item/$AUTHORITY.real_estate"
            else -> throw IllegalArgumentException("Unknown URI : $uri")
        }
    }
}