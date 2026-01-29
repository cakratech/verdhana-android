package com.bcasekuritas.rabbitmq.pool

import android.util.Log
import androidx.core.util.Pools
import com.bcasekuritas.rabbitmq.connection.BasicMQConnection
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ShutdownSignalException
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeoutException


class ChannelPooled(
    private val basicMQConnection: BasicMQConnection,
) {
    @Throws(Exception::class)
    fun obtain(): Channel? {
        val instance: Channel? = sPool.acquire()

        val channel = if (instance != null && instance.isOpen) {
            instance
        } else {
            basicMQConnection.getConnectionMQ()!!
                .createChannel()
        }
        val isPool = if (instance != null && instance.isOpen) "pool" else "create --------"
        Log.d("channelMq", "Channel Number $isPool ${channel.channelNumber}");

        return channel
    }

    fun recycle(channel: Channel) {
        // Clear state if needed.
        sPool.release(channel)
    }

    @get:Throws(Exception::class)
    val connection: Connection
        get() = this.basicMQConnection.getConnectionMQ()!!

    @Synchronized
    fun releaseChannel() {
        try {
            var holder: Channel? = sPool.acquire()
            while (holder != null) {
                try {
                    if (holder.isOpen) {
                        holder.close()
                        Log.d("channelMq", "Channel close ${holder.channelNumber}")
                    }
                } catch (e: ShutdownSignalException) {
                    // channel sudah mati / connection sudah close
                    Log.w("channelMq", "Channel already shutdown")
                }
                holder = sPool.acquire()
            }

        } catch (e: IOException) {
            Timber.e(e, e.message)
        } catch (e: TimeoutException) {
            Timber.e(e, e.message)
        }
    }

    companion object {
        private val sPool = Pools.SynchronizedPool<Channel?>(20)
    }
}
