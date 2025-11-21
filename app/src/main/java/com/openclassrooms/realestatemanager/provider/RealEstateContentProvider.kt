package com.openclassrooms.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.openclassrooms.realestatemanager.AppApplication
import com.openclassrooms.realestatemanager.data.Repository

class RealEstateContentProvider : ContentProvider() {

    companion object {
        //authority for the content provider : package name
        private const val AUTHORITY = "com.openclassrooms.realestatemanager.provider"
        
        //the bas URI that will be use to access the provider
        val CONTENT_URI: Uri = Uri.parse("content//$AUTHORITY/real_estate")

        //URI Matcher codes
        private const val REAL_ESTATES = 1
        private const val REAL_ESTATE_ID = 2

        //URI Matcher to make the difference between request for all items and only for one
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "real_estate", REAL_ESTATES)
            addURI(AUTHORITY, "real_estate/#", REAL_ESTATE_ID)
        }
    }

    private lateinit var repository: Repository

    /**
     * Initialize the content provider
     * @return true if successfully loaded
     */
    override fun onCreate(): Boolean {
        context?.let { context ->
            //TODO: pq recup repository depuis l'application context ?
            //si on passe par le context d'appApplication on le recup pas
            val application = context.applicationContext as AppApplication
            repository = application.repository
        } ?: false
        return true
    }

    /**
     * Method to handles query request
     * @return Cursor with requested data
     */
    override fun query(
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

        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor

    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(
        uri: Uri,
        values: ContentValues?
    ): Uri? {
        TODO("Not yet implemented")
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        TODO("Not yet implemented")
    }
}