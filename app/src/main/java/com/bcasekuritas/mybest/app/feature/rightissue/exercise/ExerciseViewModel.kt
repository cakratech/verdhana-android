package com.bcasekuritas.mybest.app.feature.rightissue.exercise

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.LogoutReq
import com.bcasekuritas.mybest.app.domain.dto.request.SendExerciseOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.SessionRequest
import com.bcasekuritas.mybest.app.domain.interactors.DeleteSessionsDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetIpAddressUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetLogoutUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSessionPinDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSimplePortfolioUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SendExerciseOrderUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.LogoutResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimplePortofolioResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val getSessionPin: GetSessionPinDaoUseCase,
    private val sendExerciseOrderUseCase: SendExerciseOrderUseCase,
    private val getSimplePortfolioUseCase: GetSimplePortfolioUseCase,
    private val logoutUseCase: GetLogoutUseCase,
    private val deleteSessions: DeleteSessionsDaoUseCase,
    private val getIpAddressUseCase: GetIpAddressUseCase
): BaseViewModel() {
    val getIpAddressResult = MutableLiveData<String>()
    fun getIpAddress() {
        viewModelScope.launch(Dispatchers.IO) {
            getIpAddressUseCase.invoke().collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        getIpAddressResult.postValue(resource.data?:"")
                    }
                    else -> {}
                }
            }
        }
    }

    var getSessionPinResult = MutableLiveData<Long?>()
    fun getSessionPin(userId: String){
        CoroutineScope(Dispatchers.IO).launch {
            getSessionPin.invoke(userId).collect() {resource ->
                when (resource){
                    is Resource.Success -> {
                        getSessionPinResult.postValue(resource.data)
                    }

                    else -> {}
                }
            }
        }
    }

    fun sendExerciseOrder(sendExerciseOrderRequest: SendExerciseOrderRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            sendExerciseOrderUseCase.sendOrder(sendExerciseOrderRequest)
        }
    }

    val getSimplePortfolioResult = MutableLiveData<Resource<SimplePortofolioResponse?>>()

    fun getSimplePortfolio(userId: String, sessionId: String, accNo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val simplePortfolio = SessionRequest(userId, sessionId, accNo)

            getSimplePortfolioUseCase.invoke(simplePortfolio).collect() {resource ->
                resource.let {
                    withContext(Dispatchers.Main) {
                        getSimplePortfolioResult.postValue(it)
                    }
                }
            }
        }
    }

    val getLogoutResult = MutableLiveData<LogoutResponse?>()
    fun getLogout(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val logoutRequest = LogoutReq(userId, sessionId)

            logoutUseCase.invoke(logoutRequest).collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        getLogoutResult.postValue(resource.data)
                    }
                    else -> {}
                }
            }
        }
    }

    fun deleteSession(){
        CoroutineScope(Dispatchers.IO).launch {
            deleteSessions.deleteSessionsDao()
        }
    }
}