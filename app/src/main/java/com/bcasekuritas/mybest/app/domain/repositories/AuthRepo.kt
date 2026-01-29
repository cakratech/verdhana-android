package com.bcasekuritas.mybest.app.domain.repositories

import androidx.lifecycle.LiveData
import com.bcasekuritas.mybest.app.data.entity.BiometricObject
import com.bcasekuritas.mybest.app.data.entity.SessionObject
import com.bcasekuritas.mybest.app.domain.dto.request.ChangePasswordReq
import com.bcasekuritas.mybest.app.domain.dto.request.ChangePinReq
import com.bcasekuritas.mybest.app.domain.dto.request.DeleteTrustedDeviceRequest
import com.bcasekuritas.mybest.app.domain.dto.request.LogonRequest
import com.bcasekuritas.mybest.app.domain.dto.request.LogoutReq
import com.bcasekuritas.mybest.app.domain.dto.request.SaveDeviceTokenReq
import com.bcasekuritas.mybest.app.domain.dto.request.SendOtpTrustedDeviceRequest
import com.bcasekuritas.mybest.app.domain.dto.request.TrustedDeviceReq
import com.bcasekuritas.mybest.app.domain.dto.request.ValidatePinReq
import com.bcasekuritas.mybest.app.domain.dto.request.ValidateSessionReq
import com.bcasekuritas.mybest.app.domain.dto.request.VerifyOtpTrustedDeviceRequest
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.bcas.AppNotification
import com.bcasekuritas.rabbitmq.proto.bcas.CakraMessage
import com.bcasekuritas.rabbitmq.proto.bcas.ChangePasswordResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ChangePinResponse
import com.bcasekuritas.rabbitmq.proto.bcas.DeleteDeviceResponse
import com.bcasekuritas.rabbitmq.proto.bcas.LogonResponse
import com.bcasekuritas.rabbitmq.proto.bcas.LogoutResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SaveDeviceTokenResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SendOtpResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TrustedDeviceResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ValidatePinResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ValidateSessionResponse
import com.bcasekuritas.rabbitmq.proto.bcas.VerifyOtpResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepo {

    suspend fun getLogin(logonRequest: LogonRequest): Flow<Resource<LogonResponse?>>
    suspend fun getLogout(logoutReq: LogoutReq): Flow<Resource<LogoutResponse?>>
    suspend fun getValidatePin(validatePin: ValidatePinReq): Flow<Resource<ValidatePinResponse?>>
    suspend fun getChangePassword(changePasswordReq: ChangePasswordReq): Flow<Resource<ChangePasswordResponse?>>
    suspend fun getChangePin(changePinReq: ChangePinReq): Flow<Resource<ChangePinResponse?>>
    suspend fun saveDeviceToken(saveDeviceTokenReq: SaveDeviceTokenReq?): Flow<Resource<SaveDeviceTokenResponse?>>
    suspend fun insertSession(sessionObject: SessionObject)
    suspend fun deleteSessions()
    suspend fun insertToken(biometricObject: BiometricObject)
    suspend fun updateToken(userId: String, token: String)
    suspend fun getToken(userId: String): Flow<Resource<BiometricObject>>
    suspend fun deleteBiometric()
    suspend fun deleteToken(userId: String)
    suspend fun updateSessionPin(userId: String, sessionPin: String)
    suspend fun getSessionPin(userId: String): Flow<Resource<Long>>
    suspend fun startAppNotification(cakraListener: MQMessageListener<CakraMessage>?)
    suspend fun subscribeAppNotification(routingKey: String?)
    suspend fun unsubscribeAppNotification(routingKey: String?)
    suspend fun stopAppNotification()
    suspend fun startNewAppNotification()
    suspend fun clearAppNotifLiveData()
    suspend fun validateSession(validateSessionReq: ValidateSessionReq): Flow<Resource<ValidateSessionResponse?>>

    suspend fun sendPing(userId: String)
    suspend fun startAllConsumer(isFirstLaunch: Boolean): Flow<Resource<Boolean>>

    suspend fun getIpAddress(): Flow<Resource<String>>

    val appNotificationLiveData: LiveData<AppNotification>

    suspend fun getTrustedDevice(trustedDeviceReq: TrustedDeviceReq): Flow<Resource<TrustedDeviceResponse?>>
    suspend fun sendOtpTrustedDevice(sendOtpTrustedDeviceRequest: SendOtpTrustedDeviceRequest): Flow<Resource<SendOtpResponse?>>
    suspend fun verifyOtpTrustedDevice(verifyOtpTrustedDeviceReq: VerifyOtpTrustedDeviceRequest):  Flow<Resource<VerifyOtpResponse?>>
    suspend fun deleteTrustedDevice(deleteTrustedDevice: DeleteTrustedDeviceRequest):  Flow<Resource<DeleteDeviceResponse?>>

    suspend fun closeChannel()


}