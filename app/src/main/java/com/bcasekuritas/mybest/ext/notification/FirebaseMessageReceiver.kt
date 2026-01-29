package com.bcasekuritas.mybest.ext.notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class FirebaseMessageReceiver: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.let {
            showNotification(message)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("New Firebase Token: $token")
    }

    fun showNotification(remoteMessage: RemoteMessage){

        val notificationId = System.currentTimeMillis().toInt()

        // Intent to switch to the MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val channelId = "notification_channel"

        // PendingIntent to start the next Activity
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Builder for the notificatio
        val builder = NotificationCompat.Builder(applicationContext, channelId).apply {
            setContentTitle(remoteMessage.notification?.title)
            setContentText(remoteMessage.notification?.body)
            setAutoCancel(true)
            setOnlyAlertOnce(true)
            setContentIntent(pendingIntent)
            setSmallIcon(R.drawable.logo_bcas_png)
            priority = NotificationCompat.PRIORITY_HIGH
        }

        // NotificationManager to notify the user of events in the background
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create NotificationChannel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val notificationChannel = NotificationChannel(
                channelId, "bcas_notification", NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(notificationId, builder.build())
    }
}