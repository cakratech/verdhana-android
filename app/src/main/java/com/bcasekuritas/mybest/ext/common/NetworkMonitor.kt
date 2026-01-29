package com.bcasekuritas.mybest.ext.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val connectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            // Network available - try to reconnect if we were previously connected
            Log.d("NetworkMonitor", "Network available")
        }

        override fun onLost(network: Network) {
            // Network lost
            Log.d("NetworkMonitor", "Network lost")
            // We'll rely on RabbitMQ's automatic recovery when network returns
        }
    }

    fun startMonitoring() {
        try {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
            Log.d("NetworkMonitor", "Network monitoring started")
        } catch (e: Exception) {
            Log.e("NetworkMonitor", "Failed to start network monitoring", e)
        }
    }

    fun stopMonitoring() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            Log.d("NetworkMonitor", "Network monitoring stopped")
        } catch (e: Exception) {
            Log.e("NetworkMonitor", "Failed to stop network monitoring", e)
        }
    }
}