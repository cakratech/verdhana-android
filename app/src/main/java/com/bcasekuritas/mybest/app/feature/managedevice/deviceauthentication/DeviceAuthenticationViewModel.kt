package com.bcasekuritas.mybest.app.feature.managedevice.deviceauthentication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.AccountObject
import com.bcasekuritas.mybest.app.domain.dto.request.LogonRequest
import com.bcasekuritas.mybest.app.domain.dto.request.SaveDeviceTokenReq
import com.bcasekuritas.mybest.app.domain.dto.request.SendOtpTrustedDeviceRequest
import com.bcasekuritas.mybest.app.domain.dto.request.SessionRequest
import com.bcasekuritas.mybest.app.domain.dto.request.VerifyOtpTrustedDeviceRequest
import com.bcasekuritas.mybest.app.domain.interactors.GetIpAddressUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetLoginUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSimpleAccountInfoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.InsertAccountDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SaveDeviceTokenUsecase
import com.bcasekuritas.mybest.app.domain.interactors.SendOtpTrustedDeviceUsecase
import com.bcasekuritas.mybest.app.domain.interactors.StartAllConsumerUseCase
import com.bcasekuritas.mybest.app.domain.interactors.VerifyOtpTrustedDeviceUsecase
import com.bcasekuritas.mybest.app.domain.repositories.AuthRepo
import com.bcasekuritas.mybest.app.domain.repositories.OrderRepo
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.LogonResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SaveDeviceTokenResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SendOtpResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleAccountInfoByCIFResponse
import com.bcasekuritas.rabbitmq.proto.bcas.VerifyOtpResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceAuthenticationViewModel @Inject constructor(
    private val sendOtpTrustedDeviceUsecase: SendOtpTrustedDeviceUsecase,
    private val verifyOtpTrustedDeviceUsecase: VerifyOtpTrustedDeviceUsecase,
    private val authRepo: AuthRepo,
    private val orderRepo: OrderRepo,
    private val getLoginUseCase: GetLoginUseCase,
    private val getIpAddressUseCase: GetIpAddressUseCase,
    private val getSimpleAccountInfoUseCase: GetSimpleAccountInfoUseCase,
    private val insertAccountDaoUseCase: InsertAccountDaoUseCase,
    private val saveDeviceTokenUsecase: SaveDeviceTokenUsecase,
    private val startAllConsumerUseCase: StartAllConsumerUseCase,
    ): BaseViewModel() {

    val sendOtpResult = MutableLiveData<SendOtpResponse?>()
    val verifyOtpResult = MutableLiveData<VerifyOtpResponse?>()

    fun sendOtp(request: SendOtpTrustedDeviceRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            sendOtpTrustedDeviceUsecase.invoke(request).collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        sendOtpResult.postValue(resource.data)
                    }
                    else -> {}
                }
            }
        }
    }

    fun verifyOtp(userId: String, otp: String, deviceId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = VerifyOtpTrustedDeviceRequest(userId, otp, deviceId)

            verifyOtpTrustedDeviceUsecase.invoke(request).collect() {resource ->
                resource.let {
                    when (resource) {
                        is Resource.Success -> {
                            verifyOtpResult.postValue(resource.data)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    val getLoginResult = MutableLiveData<Resource<LogonResponse?>>()
    val getIpAddressResult = MutableLiveData<String>()

    fun getLogin(userId: String, password: String, ipAddress: String, deviceId: String, deviceModel: String, deviceManufacture: String, appversion: String) {


        viewModelScope.launch(Dispatchers.IO) {

            val logonRequest = LogonRequest(userId, password, 1, "ANDROID", ipAddress, deviceId, deviceModel, deviceManufacture, useOtp = true, appversion)

            getLoginResult.postValue(Resource.Loading)
            getLoginUseCase.invoke(logonRequest).collect { resource ->
                resource.let {
                    getLoginResult.postValue(it)
                }
            }
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

    val getSimpleAccountResult = MutableLiveData<Resource<SimpleAccountInfoByCIFResponse?>>()
    fun getSimpleAccInfo(userId: String, sessionId: String, cifCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val simpleAccInfo =
                SessionRequest(userId = userId, sessionId = sessionId, cifCode = cifCode)

            getSimpleAccountInfoUseCase.invoke(simpleAccInfo).collect() { resource ->
                resource.let {
//                    async {
                    getSimpleAccountResult.postValue(it)
                    if (it is Resource.Success && it.data?.status == 0) {
                        it.data.simpleAccountInfoList?.forEach { accMap ->
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

    val saveTokenDeviceResult = MutableLiveData<Resource<SaveDeviceTokenResponse?>>()
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

    val startAllConsumerResult = MutableLiveData<Boolean?>()
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

}