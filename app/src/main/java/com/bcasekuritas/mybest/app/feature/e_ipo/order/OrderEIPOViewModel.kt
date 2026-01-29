package com.bcasekuritas.mybest.app.feature.e_ipo.order

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.mapper.toIpoData
import com.bcasekuritas.mybest.app.domain.dto.request.EipoOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IPOInfoRequest
import com.bcasekuritas.mybest.app.domain.dto.request.LogoutReq
import com.bcasekuritas.mybest.app.domain.dto.response.IpoData
import com.bcasekuritas.mybest.app.domain.interactors.DeleteSessionsDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetIPOInfoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetIpAddressUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetLogoutUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSessionPinDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SendEipoOrderUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.LogoutResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderEIPOViewModel @Inject constructor(
    private val getSessionPin: GetSessionPinDaoUseCase,
    private val sendEipoOrderUseCase: SendEipoOrderUseCase,
    private val logoutUseCase: GetLogoutUseCase,
    private val deleteSessions: DeleteSessionsDaoUseCase,
    private val getIPOInfoUseCase: GetIPOInfoUseCase,
    private val getIpAddressUseCase: GetIpAddressUseCase
): BaseViewModel() {

    var getSessionPinResult = MutableLiveData<Long?>()
    val getLogoutResult = MutableLiveData<LogoutResponse?>()
    val getIpoInfoResult = MutableLiveData<IpoData>()
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

    fun sendOrder(eipoOrderRequest: EipoOrderRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            sendEipoOrderUseCase.sendOrder(eipoOrderRequest)
        }
    }

    fun getIpoInfo(userId: String, sessionId: String, ipoCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = IPOInfoRequest(userId, sessionId, ipoCode)

            getIPOInfoUseCase.invoke(request).collect() {resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            val response = it.data?.pipelinesIpoListData
                            if (response != null) {
                                getIpoInfoResult.postValue(response.toIpoData())
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

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