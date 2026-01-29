package com.bcasekuritas.mybest.ext.common

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.bcasekuritas.rabbitmq.connection.BasicMQConnection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLifecycleObserver @Inject constructor(
    private val serviceManager: ServiceLifecycleManager
) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        // App is moving to foreground
        serviceManager.stopService()
        Log.d("AppLifecycle", "App moved to foreground")
    }

    override fun onStop(owner: LifecycleOwner) {
        // App is moving to background
        serviceManager.startService()
        Log.d("AppLifecycle", "App moved to background")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        // Make sure to stop the service when app is destroyed
        serviceManager.stopService()
    }
}