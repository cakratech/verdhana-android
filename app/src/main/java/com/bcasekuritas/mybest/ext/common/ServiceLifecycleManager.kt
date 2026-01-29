package com.bcasekuritas.mybest.ext.common

import android.app.ActivityManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceLifecycleManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun startService() {
        RabbitMQForegroundService.startService(context)
    }

    fun stopService() {
//        RabbitMQForegroundService.stopService(context)
    }

    fun isServiceRunning(): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == RabbitMQForegroundService::class.java.name }
    }
}