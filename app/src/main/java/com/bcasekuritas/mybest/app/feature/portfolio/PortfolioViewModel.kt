package com.bcasekuritas.mybest.app.feature.portfolio

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.LogoutReq
import com.bcasekuritas.mybest.app.domain.dto.request.SessionRequest
import com.bcasekuritas.mybest.app.domain.interactors.DeleteSessionsDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetAccNameDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetLogoutUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSessionPinDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSimplePortfolioUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.LogoutResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimplePortofolioResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val getSessionPin: GetSessionPinDaoUseCase,
    private val getAccNameDaoUseCase: GetAccNameDaoUseCase,
    private val logoutUseCase: GetLogoutUseCase,
    private val getSimplePortfolioUseCase: GetSimplePortfolioUseCase,
    private val deleteSessions: DeleteSessionsDaoUseCase
): BaseViewModel() {

    var getSessionPinResult = MutableLiveData<Long?>()
    var getAccNameDaoResult = MutableLiveData<String>()
    val getSimplePortfolioResult = MutableLiveData<Resource<SimplePortofolioResponse?>>()
    val getLogoutResult = MutableLiveData<LogoutResponse?>()

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

    fun clearSessionPin() {
        getSessionPinResult.value = null
    }

    fun getAccNameDao(accNo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getAccNameDaoUseCase.invoke(accNo).collect(){
                when(it){
                    is Resource.Success -> {
                        getAccNameDaoResult.postValue(it.data ?: "")
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