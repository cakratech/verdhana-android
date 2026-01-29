package com.bcasekuritas.rabbitmq.connection

import androidx.lifecycle.LiveData
import com.bcasekuritas.rabbitmq.common.ResetSingleLiveEvent
import java.util.concurrent.CountDownLatch

interface IMQConnectionListener {

    val connListenerLiveData: LiveData<String>

    fun onListener(status: String)

    val latchData: CountDownLatch?

    fun doLatch()

    fun countLatch()

    val isSessionExpiredLiveData: ResetSingleLiveEvent<Boolean>

    val isPinExpiredLiveData: ResetSingleLiveEvent<Boolean>

    val timeOutLiveData: ResetSingleLiveEvent<String>
}
