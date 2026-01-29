package com.bcasekuritas.mybest.app.feature.activity.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.ValidateSessionReq
import com.bcasekuritas.mybest.app.domain.interactors.DeleteSessionsDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetValidateSessionUseCase
import com.bcasekuritas.mybest.app.domain.interactors.StartAllConsumerUseCase
import com.bcasekuritas.mybest.app.domain.repositories.AuthRepo
import com.bcasekuritas.mybest.app.domain.repositories.OrderRepo
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.ValidateSessionResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val deleteSessions: DeleteSessionsDaoUseCase,
    private val validateSessionUseCase: GetValidateSessionUseCase,
    private val startAllConsumerUseCase: StartAllConsumerUseCase,
    private val authRepo: AuthRepo,
    private val orderRepo: OrderRepo
): BaseViewModel() {
    val getValidateSessionResult = MutableLiveData<ValidateSessionResponse?>()
    val startAllConsumerResult = MutableLiveData<Boolean?>()

    fun validateSession(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val validateSessionRequest = ValidateSessionReq(userId, sessionId)

            validateSessionUseCase.invoke(validateSessionRequest).collect() {resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            getValidateSessionResult.postValue(it.data)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun deleteSession(){
        CoroutineScope(Dispatchers.IO).launch {
            deleteSessions.deleteSessionsDao()
        }
    }

    fun startNewAppNotification() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepo.startNewAppNotification()
        }
    }
    fun startNewOrderReply() {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepo.startNewOrderReply()
        }
    }

    fun startSubsAll(param: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            startAllConsumerUseCase.invoke(true).collect() { resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            it.data?.let { res ->
                                if (res) {
                                    startAllConsumerResult.postValue(param)
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}