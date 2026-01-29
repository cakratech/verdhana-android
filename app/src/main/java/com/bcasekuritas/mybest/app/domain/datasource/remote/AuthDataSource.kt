package com.bcasekuritas.mybest.app.domain.datasource.remote

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
import com.bcasekuritas.rabbitmq.message.MQMessageListener
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


interface AuthDataSource {

    suspend fun getLogin(logonRequest: LogonRequest): LogonResponse?
    suspend fun getLogout(logoutReq: LogoutReq): LogoutResponse?
    suspend fun insertSession(sessionObject: SessionObject)
    suspend fun deleteSessions()
    suspend fun getSessionPin(userId: String): Long
    suspend fun updateSessionPin(userId: String, sessionPin: String)

    suspend fun insertToken(biometricObject: BiometricObject)
    suspend fun getToken(userId: String): BiometricObject
    suspend fun updateToken(userId: String, token: String)
    suspend fun deleteBiometric()
    suspend fun deleteToken(userId: String)

    suspend fun startAppNotification(cakraListener: MQMessageListener<CakraMessage>?)
    suspend fun subscribeAppNotification(routingKey: String?)
    suspend fun unsubscribeAppNotification(routingKey: String?)
    suspend fun startNewAppNotification(appNotificationFlow: (CakraMessage) -> Unit)
    suspend fun stopAppNotification()
    suspend fun getValidatePin(validatePinRequest: ValidatePinReq): ValidatePinResponse?
    suspend fun getChangePassword(changePasswordReq: ChangePasswordReq): ChangePasswordResponse?
    suspend fun getChangePin(changePINRequest: ChangePinReq?): ChangePinResponse?

    suspend fun saveDeviceToken(saveDeviceTokenReq: SaveDeviceTokenReq?): SaveDeviceTokenResponse?
    suspend fun getValidateSession(validateSessionReq: ValidateSessionReq): ValidateSessionResponse?

    suspend fun sendPing(userId: String)
    suspend fun startAllConsumer(isFirstLaunch: Boolean): Boolean

    suspend fun getIpAddress(): String

    suspend fun getTrustedDevice(trustedDeviceReq: TrustedDeviceReq): TrustedDeviceResponse?
    suspend fun sendOtpTrustedDevice(sendOtpTrustedDeviceRequest: SendOtpTrustedDeviceRequest): SendOtpResponse?
    suspend fun verifyOtpTrustedDevice(verifyOtpTrustedDeviceReq: VerifyOtpTrustedDeviceRequest): VerifyOtpResponse?
    suspend fun deleteTrustedDevice(deleteTrustedDevice: DeleteTrustedDeviceRequest): DeleteDeviceResponse?

    suspend fun closeChannel()

}