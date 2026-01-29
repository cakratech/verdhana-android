package com.bcasekuritas.rabbitmq.connection

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bcasekuritas.rabbitmq.common.ResetSingleLiveEvent
import java.util.concurrent.CountDownLatch

class MQConnectionListener : IMQConnectionListener {

    private var _connListenerLiveData = MutableLiveData<String>()
    private var _latchData: CountDownLatch? = null

    override fun onListener(status: String) {
        val currentTime = System.currentTimeMillis()
        val formattedTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", java.util.Locale.getDefault())
            .format(java.util.Date(currentTime))

        Log.d("MQConnectionListener", "LiveData update at $formattedTime, status = $status")
        _connListenerLiveData.postValue(status)
    }

    override val connListenerLiveData: LiveData<String>
        get() = _connListenerLiveData

    override fun doLatch() {
        _latchData = CountDownLatch(1)
    }

    override fun countLatch() {
        _latchData?.countDown()
    }

    override val latchData: CountDownLatch?
        get() = _latchData

    override val isSessionExpiredLiveData: ResetSingleLiveEvent<Boolean> = ResetSingleLiveEvent()

    override val isPinExpiredLiveData: ResetSingleLiveEvent<Boolean> = ResetSingleLiveEvent()

    override val timeOutLiveData: ResetSingleLiveEvent<String> = ResetSingleLiveEvent()
}
