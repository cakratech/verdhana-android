package com.bcasekuritas.mybest.app.feature.profile.profilelanding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.AccountObject
import com.bcasekuritas.mybest.app.data.entity.BiometricObject
import com.bcasekuritas.mybest.app.domain.dto.request.LogoutReq
import com.bcasekuritas.mybest.app.domain.dto.request.SessionRequest
import com.bcasekuritas.mybest.app.domain.interactors.DeleteBiometricDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.DeleteSessionsDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.DeleteTokenDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetAccountInfoDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetBiometricDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetLogoutUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSessionPinDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSimplePortfolioUseCase
import com.bcasekuritas.mybest.app.domain.interactors.InsertBiometricDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UpdateTokenBiometricDaoUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.proto.bcas.LogoutResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimplePortofolioResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val deleteSessions: DeleteSessionsDaoUseCase,
    private val getSessionPin: GetSessionPinDaoUseCase,
    private val getSimplePortfolioUseCase: GetSimplePortfolioUseCase,
    private val getAccountInfoDaoUseCase: GetAccountInfoDaoUseCase,
    private val logoutUseCase: GetLogoutUseCase,
    private val insertTokenUseCase: InsertBiometricDaoUseCase,
    private val getTokenUseCase: GetBiometricDaoUseCase,
    private val updateToken: UpdateTokenBiometricDaoUseCase,
    private val deleteBiometricUseCase: DeleteBiometricDaoUseCase,
    private val deleteTokenDaoUseCase: DeleteTokenDaoUseCase,
    val imqConnectionListener: IMQConnectionListener
): BaseViewModel() {
    var getSessionPinResult = MutableLiveData<Long?>()
    val getSimplePortfolioResult by lazy { MutableLiveData<Resource<SimplePortofolioResponse?>>() }
    var getAccountInfoResult = MutableLiveData<AccountObject>()
    var getTokenResult = MutableLiveData<BiometricObject?>()
    val getLogoutResult = MutableLiveData<LogoutResponse?>()

    fun clearSessionPinResult() {
        getSessionPinResult.value = null
    }

    fun deleteSession(){
        CoroutineScope(Dispatchers.IO).launch {
            deleteSessions.deleteSessionsDao()
        }
    }

    fun deleteBiometric(){
        CoroutineScope(Dispatchers.IO).launch {
            deleteBiometricUseCase.deleteBiometricDao()
        }
    }

    fun deleteToken(userId: String){
        CoroutineScope(Dispatchers.IO).launch {
            deleteTokenDaoUseCase.deleteTokenDao(userId)
        }
    }

    fun getAccNameDao(accNo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getAccountInfoDaoUseCase.invoke(accNo).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { data ->
                            withContext(Dispatchers.Main) {
                                getAccountInfoResult.postValue(data)
                            }
                        }
                    }

                    else -> {
                        // Handle other cases if needed
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

    fun insertToken(biometricObject: BiometricObject) {
        viewModelScope.launch {
            insertTokenUseCase.insertToken(biometricObject)
        }
    }

    fun updateToken(userId: String, token: String) {
        viewModelScope.launch {
            updateToken.updateToken(userId, token)
        }
    }

    fun getToken(userId: String){
        CoroutineScope(Dispatchers.IO).launch {
            getTokenUseCase.invoke(userId).collect() {resource ->
                when (resource){
                    is Resource.Success -> {
                        getTokenResult.postValue(resource.data)
                    }

                    else -> {}
                }
            }
        }
    }

    fun clearGetToken(){
        getTokenResult.postValue(null)
    }

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
}