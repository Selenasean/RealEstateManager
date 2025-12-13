package com.openclassrooms.realestatemanager.domain.notifications

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

import com.openclassrooms.realestatemanager.R

class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "real_estates_channel"
        private const val CHANNEL_NAME = "Real Estate Updates"
        private const val NOTIFICATION_ID_CREATED = 1
        private const val NOTIFICATION_ID_UPDATED = 2
    }

    private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)

    init {
        createNotificationChannel()
    }

    /**
     * Create notification channel : so your channel can be register with the system
     */
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Notifications for real estate creation and updates"
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    /**
     * Build the notification to send when real estate is created
     * notify android system to display the notification
     */
    fun sendCreationNotification() {
        val creationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logement)
            .setContentTitle(context.getString(R.string.creation_notif))
            .setContentText(context.getString(R.string.real_estate_created))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) //to cancel the notification when user clicks on it

        sendNotification(creationBuilder.build(), NOTIFICATION_ID_CREATED)

    }

    /**
     * Build the notification to send when real estate is updated
     * notify android system to display the notification
     */
    fun sendUpdateNotification() {
        Log.i("notification", "sendUpdateNotification: toggle method ")
        val updateBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logement)
            .setContentTitle(context.getString(R.string.update_notif))
            .setContentText(context.getString(R.string.real_estate_update))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) //to cancel the notification when user clicks on it

        sendNotification(updateBuilder.build(), NOTIFICATION_ID_UPDATED)
    }

    private fun sendNotification(notification: Notification, id: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED) {
                  notificationManager.notify(id, notification)
            }
        } else {
            notificationManager.notify(id, notification)
        }


    }
}
