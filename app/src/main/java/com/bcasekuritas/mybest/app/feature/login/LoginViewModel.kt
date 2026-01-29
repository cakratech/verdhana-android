package com.bcasekuritas.mybest.app.feature.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.AccountObject
import com.bcasekuritas.mybest.app.data.entity.BiometricObject
import com.bcasekuritas.mybest.app.domain.dto.request.LogonRequest
import com.bcasekuritas.mybest.app.domain.dto.request.SaveDeviceTokenReq
import com.bcasekuritas.mybest.app.domain.dto.request.SessionRequest
import com.bcasekuritas.mybest.app.domain.interactors.ClearStockNotationDB
import com.bcasekuritas.mybest.app.domain.interactors.CloseChannelUseCase
import com.bcasekuritas.mybest.app.domain.interactors.DeleteBiometricDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.DeleteSessionsDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetBannerLoginUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetBiometricDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetCashPosUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetIpAddressUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetLoginUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSimpleAccountInfoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.InsertAccountDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.InsertBiometricDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SaveDeviceTokenUsecase
import com.bcasekuritas.mybest.app.domain.interactors.StartAllConsumerUseCase
import com.bcasekuritas.mybest.app.domain.interactors.StopCIFStockPosUseCase
import com.bcasekuritas.mybest.app.domain.interactors.StopIndiceDataUserCase
import com.bcasekuritas.mybest.app.domain.interactors.StopOrderBookListUseCase
import com.bcasekuritas.mybest.app.domain.interactors.StopRunningTradeUseCase
import com.bcasekuritas.mybest.app.domain.interactors.StopTradeSumUserCase
import com.bcasekuritas.mybest.app.domain.interactors.UpdateTokenBiometricDaoUseCase
import com.bcasekuritas.mybest.app.domain.repositories.AuthRepo
import com.bcasekuritas.mybest.app.domain.repositories.OrderRepo
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.LogonResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SaveDeviceTokenResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleAccountInfoByCIFResponse
import com.bcasekuritas.rabbitmq.proto.news.LoginBannerResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel  @Inject constructor(
    private val getLoginUseCase: GetLoginUseCase,
    private val getSimpleAccountInfoUseCase: GetSimpleAccountInfoUseCase,
    private val insertAccountDaoUseCase: InsertAccountDaoUseCase,
    private val deleteSessions: DeleteSessionsDaoUseCase,
    private val saveDeviceTokenUsecase: SaveDeviceTokenUsecase,
    private val insertTokenUseCase: InsertBiometricDaoUseCase,
    private val getTokenUseCase: GetBiometricDaoUseCase,
    private val updateToken: UpdateTokenBiometricDaoUseCase,
    private val deleteTokenUseCase: DeleteBiometricDaoUseCase,
    private val clearStockNotationDBUseCase: ClearStockNotationDB,
    private val getBannerLoginUseCase: GetBannerLoginUseCase,
    private val startAllConsumerUseCase: StartAllConsumerUseCase,
    private val authRepo: AuthRepo,
    private val orderRepo: OrderRepo,

    private val stopRunningTradeUseCase: StopRunningTradeUseCase,
    private val stopOrderBookListUseCase: StopOrderBookListUseCase,
    private val stopIndiceDataUserCase: StopIndiceDataUserCase,
    private val stopTradeSummaryUserCase: StopTradeSumUserCase,
    private val stopCIFStockPosUseCase: StopCIFStockPosUseCase,

    private val getCashPosUseCase: GetCashPosUseCase,
    private val getIpAddressUseCase: GetIpAddressUseCase,
    private val closeChannelUseCase: CloseChannelUseCase
): BaseViewModel() {

    val getLoginResult = MutableLiveData<Resource<LogonResponse?>>()
    var getTokenResult = MutableLiveData<BiometricObject?>()
    val saveTokenDeviceResult = MutableLiveData<Resource<SaveDeviceTokenResponse?>>()
    val startAllConsumerResult = MutableLiveData<Boolean?>()
    val getIpAddressResult = MutableLiveData<String>()

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

    fun deleteSession(){
        CoroutineScope(Dispatchers.IO).launch {
            deleteSessions.deleteSessionsDao()
        }
    }

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

    fun getLogin(userId: String, password: String, ipAddress: String, deviceId: String, deviceModel: String, deviceManufacture: String, appVersion: String) {


        viewModelScope.launch(Dispatchers.IO) {

            val logonRequest = LogonRequest(userId, password, 1, "ANDROID", ipAddress, deviceId, deviceModel, deviceManufacture, useOtp = false, appVersion)

            getLoginResult.postValue(Resource.Loading)
            getLoginUseCase.invoke(logonRequest).collect { resource ->
                resource.let {
                    getLoginResult.postValue(it)
                }
            }
        }
    }

    val getBannerLoginResult = MutableLiveData<Resource<LoginBannerResponse?>>()

    fun getBannerLogin() {
        viewModelScope.launch(Dispatchers.IO) {
            getBannerLoginUseCase.invoke().collect() {resource ->
                resource.let {
                    getBannerLoginResult.postValue(it)
                }
            }
        }

    }

    fun saveFCMToken(userId: String, sessionId: String, fcmToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val saveDeviceTokenReq = SaveDeviceTokenReq(userId, sessionId, fcmToken)
            saveDeviceTokenUsecase.invoke(saveDeviceTokenReq).collect() {resource ->
                resource.let {
                    saveTokenDeviceResult.postValue(it)
                }
            }
        }

    }


    val getSimpleAccountResult = MutableLiveData<Resource<SimpleAccountInfoByCIFResponse?>>()
    fun getSimpleAccInfo(userId: String, sessionId: String, cifCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val simpleAccInfo = SessionRequest(userId = userId, sessionId = sessionId, cifCode = cifCode)

            getSimpleAccountInfoUseCase.invoke(simpleAccInfo).collect() {resource ->
                resource.let {
//                    async {
                        getSimpleAccountResult.postValue(it)
                        if (it is Resource.Success && it.data?.status == 0) {
                            it.data.simpleAccountInfoList?.forEach {accMap ->
                                val accountObject = AccountObject(
                                    accNo = accMap.accno,
                                    accName = accMap.accname,
                                    userId = userId,
                                    cifCode = accMap.cifCode,
                                    productType = "",
                                    clientName = ""
                                )
                                insertAccountDaoUseCase.insertAccountDao(accountObject)
                            }
                        }
//                    }
                }
            }
        }

    }

    fun insertAccountDao(accountObject: AccountObject) {
        viewModelScope.launch(Dispatchers.IO) {
            insertAccountDaoUseCase.insertAccountDao(accountObject)
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
                        if (resource.data?.userId == ""){
                            getTokenResult.postValue(null)
                        } else {
                            getTokenResult.postValue(resource.data)
                        }
                    }

                    else -> {}
                }
            }
        }
    }
    fun deleteToken(){
        CoroutineScope(Dispatchers.IO).launch {
            deleteTokenUseCase.deleteBiometricDao()
        }
    }


    fun deleteDBStockNotation() {
        viewModelScope.launch(Dispatchers.IO) {
            clearStockNotationDBUseCase.clearStockNotationDB()
        }
    }

    fun startSubsAll() {
        viewModelScope.launch(Dispatchers.IO) {
            startAllConsumerUseCase.invoke(true).collect() { resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            it.data?.let { res ->
                                if (res) {
                                    startAllConsumerResult.postValue(res)
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun stopRunningTrade() {
        viewModelScope.launch(Dispatchers.IO) {
            stopRunningTradeUseCase.stopRunningTrade()
        }
    }

    fun stopOrderBook() {
        viewModelScope.launch(Dispatchers.IO) {
            stopOrderBookListUseCase.stopOrderBook()
        }
    }

    fun stopIndiceData() {
        viewModelScope.launch(Dispatchers.IO) {
            stopIndiceDataUserCase.stopIndiceData()
        }
    }

    fun stopTradeSummary() {
        viewModelScope.launch(Dispatchers.IO) {
            stopTradeSummaryUserCase.stopTradeSum()
        }
    }

    fun stopCIFStockPos() {
        viewModelScope.launch(Dispatchers.IO) {
            stopCIFStockPosUseCase.stopCIFStockPos()
        }
    }

    fun closeChannel() {
        viewModelScope.launch(Dispatchers.IO) {
            closeChannelUseCase.close()
        }
    }

}


