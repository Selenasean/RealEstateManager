package com.openclassrooms.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.openclassrooms.realestatemanager.AppApplication
import com.openclassrooms.realestatemanager.data.GeocoderRepository
import com.openclassrooms.realestatemanager.data.Repository

class RealEstateContentProvider : ContentProvider() {


    private lateinit var handlerContentProvider: HandlerContentProvider

    /**
     * Initialize the content provider and his handler
     * this is where we create our real dependencies
     */
    override fun onCreate(): Boolean {
        context?.let { context ->
            val application = context.applicationContext as AppApplication

            val repository = Repository(
                application.appDataBase,
                GeocoderRepository(context)
            )

            handlerContentProvider = HandlerContentProvider(repository = repository)

        } ?: false
        return true
    }

    /**
     * Method to handles query request
     * @return Cursor with requested data
     * Using HandlerContentProvider
     */
    override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor {

        val cursor = handlerContentProvider.query(
            uri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor

    }

    /**
     * Returns the MIME type for the data at the given URI
     * using HandlerContentProvider
     */
    override fun getType(uri: Uri): String {
        return handlerContentProvider.getType(uri)
    }

    override fun insert(
        uri: Uri,
        values: ContentValues?
    ): Uri {
        throw IllegalArgumentException("Not implemented: Use the app to insert data.")
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        throw IllegalArgumentException("Not implemented: Use the app to delete data.")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        throw IllegalArgumentException("Not implemented: Use the app to update data.")
    }
}