package com.bcasekuritas.mybest.ext.common

import com.bcasekuritas.mybest.app.domain.interactors.SendPingUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Timer
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.scheduleAtFixedRate

@Singleton
class PingHandler @Inject constructor(
    private val sendPingUseCase: SendPingUseCase
) {

    private var isActive = false
    private var timer: Timer? = null

    private var coroutineScope: CoroutineScope? = null

    fun startTimer(userId: String, sessionId: String) {
        if (!isActive) {
            isActive = true
            if (coroutineScope == null) {
                coroutineScope = CoroutineScope(Dispatchers.IO + Job())
            }
            if (timer == null) {
                timer = Timer()
            }
            coroutineScope?.launch {
                timer?.scheduleAtFixedRate(25000, 25000) {
                    coroutineScope?.launch {
                        try {
                            if (sessionId != "") {
                                sendPingUseCase.sendPing(userId.lowercase())
                            } else {
                                coroutineScope?.cancel()
                                coroutineScope = null
                                timer?.cancel()
                                timer = null
                            }
                        } catch (e: Exception) {
                            // Handle exception
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    fun stopTimer() {
        isActive = false
        coroutineScope?.cancel()
        coroutineScope = null
        timer?.cancel()
        timer = null
    }
}