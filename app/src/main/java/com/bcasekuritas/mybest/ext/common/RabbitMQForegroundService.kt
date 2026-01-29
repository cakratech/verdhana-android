package com.bcasekuritas.mybest.ext.common

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bcasekuritas.mybest.R
import com.bcasekuritas.rabbitmq.connection.BasicMQConnection
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class RabbitMQForegroundService: Service() {

    private var isRunning = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
//                 Start as foreground service to keep connection alive
                if (!isRunning) {
                    isRunning = true
                    val notification = createMinimalUserFriendlyNotification()
                    startForeground(NOTIFICATION_ID, notification)
                    Log.d("RabbitMQService", "Service started")
                }
            }
            ACTION_STOP -> {
                if (isRunning) {
                    stopService()
                    isRunning = false
                }
            }
        }
        return START_STICKY
    }

    private fun createMinimalUserFriendlyNotification(): Notification {
        return NotificationCompat.Builder(this, createChannel()).apply {
            setContentTitle("BCA Sekuritas Connected")
            setContentText("Your Session is running in the background")
            setShowWhen(false)
            setOngoing(true)
            setCategory(Notification.CATEGORY_SERVICE)
            setSmallIcon(R.drawable.logo_bcas_png)
            priority = NotificationCompat.PRIORITY_LOW
        }.build()
    }

    private fun createChannel(): String {
        val channel = NotificationChannel(
            "background_connection",
            "Background Services",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Background connection maintenance"
            setShowBadge(false)
            lockscreenVisibility = Notification.VISIBILITY_SECRET
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        return "background_connection"
    }

    private fun stopService() {
        stopForeground(true)
        stopSelf()
        Log.d("RabbitMQService", "Service stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val NOTIFICATION_ID = 1001

        fun startService(context: Context) {
            if (!context.isAppInForeground()) return
            val intent = Intent(context, RabbitMQForegroundService::class.java).apply {
                action = ACTION_START
            }
            try {
                context.startForegroundService(intent)
            } catch (ignore: Exception) {}
        }

        fun stopService(context: Context) {
            val intent = Intent(context, RabbitMQForegroundService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

}