package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.data.dao.BiometricDao
import com.bcasekuritas.mybest.app.data.dao.SessionDao
import com.bcasekuritas.mybest.app.data.entity.BiometricObject
import com.bcasekuritas.mybest.app.data.entity.SessionObject
import com.bcasekuritas.mybest.app.domain.datasource.remote.AuthDataSource
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
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.bcas.CakraHeartbeat
import com.bcasekuritas.rabbitmq.proto.bcas.CakraMessage
import com.bcasekuritas.rabbitmq.proto.bcas.ChangePasswordRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ChangePasswordResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ChangePinRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ChangePinResponse
import com.bcasekuritas.rabbitmq.proto.bcas.DeleteDeviceRequest
import com.bcasekuritas.rabbitmq.proto.bcas.DeleteDeviceResponse
import com.bcasekuritas.rabbitmq.proto.bcas.LogonResponse
import com.bcasekuritas.rabbitmq.proto.bcas.LogoutRequest
import com.bcasekuritas.rabbitmq.proto.bcas.LogoutResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SaveDeviceTokenRequest
import com.bcasekuritas.rabbitmq.proto.bcas.SaveDeviceTokenResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SendOtpRequest
import com.bcasekuritas.rabbitmq.proto.bcas.SendOtpResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TrustedDeviceRequest
import com.bcasekuritas.rabbitmq.proto.bcas.TrustedDeviceResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ValidatePinRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ValidatePinResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ValidateSessionRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ValidateSessionResponse
import com.bcasekuritas.rabbitmq.proto.bcas.VerifyOtpRequest
import com.bcasekuritas.rabbitmq.proto.bcas.VerifyOtpResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import com.bcasekuritas.rabbitmq.proto.bcas.LogonRequest as LoginRequestMQ

class AuthDataSourceImpl @Inject constructor(
    private val oltService: OLTService,
    private val localSourceDataSession: SessionDao,
    private val localSourceDataBiometric: BiometricDao
) : AuthDataSource {
    override suspend fun getLogin(logonRequest: LogonRequest): LogonResponse? {
        val logonReq = LoginRequestMQ
            .newBuilder()
            .setUserId(logonRequest.userId)
            .setPassword(logonRequest.passowrd)
            .setType(logonRequest.type)
            .setDeviceType(logonRequest.deviceType)
            .setIp(logonRequest.ip)
            .setDeviceId(logonRequest.deviceId)
            .setDeviceModel(logonRequest.deviceModel)
            .setManufacture(logonRequest.deviceManufacture)
            .setAppVersion(logonRequest.appVersion)
            .build()

        return oltService.logon(logonReq)
    }

    override suspend fun getLogout(logoutReq: LogoutReq): LogoutResponse? {
        val logoutRequest = LogoutRequest.newBuilder()
            .setUserId(logoutReq.userId)
            .setSessionId(logoutReq.sessionId)
            .build()

        return oltService.logout(logoutRequest)
    }

    override suspend fun getValidatePin(validatePinRequest: ValidatePinReq): ValidatePinResponse? {
        val validatePinReq = ValidatePinRequest
            .newBuilder()
            .setUserId(validatePinRequest.userId)
            .setPinValue(validatePinRequest.pinValue)
            .setSessionId(validatePinRequest.sessionId)
            .setRememberedPin(validatePinRequest.rememberedPin)
            .build()

        return oltService.getValidatePin(validatePinReq)
    }

    override suspend fun getChangePassword(changePasswordReq: ChangePasswordReq): ChangePasswordResponse? {
        val changePasswordReq = ChangePasswordRequest
            .newBuilder()
            .setUserId(changePasswordReq.userId)
            .setOldPass(changePasswordReq.oldPass)
            .setNewPass(changePasswordReq.newPass)
            .setConfirmPass(changePasswordReq.confirmPass)
            .build()

        return oltService.getChangePassword(changePasswordReq)
    }

    override suspend fun getChangePin(changePINRequest: ChangePinReq?): ChangePinResponse? {
        val changePinRequest = ChangePinRequest
            .newBuilder()
            .setUserId(changePINRequest?.userId)
            .setOldPin(changePINRequest?.oldPin)
            .setNewPin(changePINRequest?.newPin)
            .setConfirmPin(changePINRequest?.confirmPin)
            .build()

        return oltService.getChangePin(changePinRequest)
    }

    override suspend fun saveDeviceToken(saveDeviceTokenReq: SaveDeviceTokenReq?): SaveDeviceTokenResponse? {

        val saveDeviceTokenRequest = SaveDeviceTokenRequest
            .newBuilder()
            .setUserId(saveDeviceTokenReq?.userId)
            .setSessionId(saveDeviceTokenReq?.sessionId)
            .setToken(saveDeviceTokenReq?.token)
            .build()

        return oltService.saveDeviceToken(saveDeviceTokenRequest)
    }

    override suspend fun getValidateSession(validateSessionReq: ValidateSessionReq): ValidateSessionResponse? {
        val validateSessionRequesst = ValidateSessionRequest.newBuilder()
            .setUserId(validateSessionReq.userId)
            .setSessionId(validateSessionReq.sessionId)
            .build()

        return oltService.getValidateSession(validateSessionRequesst)
    }

    override suspend fun sendPing(userId: String) {
        val sendPringReq = CakraHeartbeat.newBuilder()
            .setUserId(userId)
            .build()

        return oltService.sendPing(sendPringReq)
    }

    override suspend fun insertSession(sessionObject: SessionObject) {
        sessionObject.let {
            localSourceDataSession.insertSession(
                SessionObject(
                    userId = it.userId,
                    sessionId = it.sessionId,
                    sessionPin = it.sessionPin
                )
            )
        }
    }

    override suspend fun insertToken(biometricObject: BiometricObject) {
        biometricObject.let {
            localSourceDataBiometric.insertToken(
                BiometricObject(
                    userId = it.userId,
                    pw = it.pw,
                    token = it.token,
                )
            )
        }
    }

    override suspend fun updateToken(userId: String, token: String) {
        return localSourceDataBiometric.updateToken(userId, token)
    }

    override suspend fun getToken(userId: String): BiometricObject {
        return localSourceDataBiometric.getToken(userId)
    }



    override suspend fun deleteBiometric() {
        localSourceDataBiometric.deleteBiometric()
    }

    override suspend fun deleteToken(userId: String) {
        localSourceDataBiometric.deleteToken(userId)
    }

    override suspend fun deleteSessions() {
        localSourceDataSession.deleteSessions()
    }

    override suspend fun updateSessionPin(userId: String, sessionPin: String) {
        localSourceDataSession.updateSessionPin(userId, sessionPin)
    }

    override suspend fun getSessionPin(userId: String): Long {
        return localSourceDataSession.getSessionPin(userId)
    }

    override suspend fun startAppNotification(
        cakraListener: MQMessageListener<CakraMessage>?
    ) {
        oltService.startAppNotification(cakraListener)
    }

    override suspend fun unsubscribeAppNotification(routingKey: String?) {
        oltService.unsubscribeAppNotification(routingKey)
    }

    override suspend fun subscribeAppNotification(routingKey: String?) {
        oltService.subscribeAppNotification(routingKey)
    }

    override suspend fun stopAppNotification() {
        oltService.stopAppNotification()
    }

    override suspend fun startNewAppNotification(appNotificationFlow: (CakraMessage) -> Unit) {
        oltService.startNewAppNotification(appNotificationFlow)
    }

    override suspend fun startAllConsumer(isFirstLaunch: Boolean): Boolean {
        return oltService.startAllConsumer(isFirstLaunch)
    }

    override suspend fun getIpAddress(): String {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.ipify.org")
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string() ?: ""
            } else {
                Timber.d("Failed to get IP: ${response.message}")
                ""
            }
        } catch (e: IOException) {
            Timber.d("Exception getting IP: ${e.message}")
            ""
        }
    }

    override suspend fun getTrustedDevice(trustedDeviceReq: TrustedDeviceReq): TrustedDeviceResponse? {
        val request = TrustedDeviceRequest.newBuilder()
            .setUserId(trustedDeviceReq.userId)
            .setSessionId(trustedDeviceReq.sessionId)
            .build()

        return oltService.getTrustedDevice(request)
    }

    override suspend fun sendOtpTrustedDevice(sendOtpTrustedDeviceRequest: SendOtpTrustedDeviceRequest): SendOtpResponse? {
        val request = SendOtpRequest.newBuilder()
            .setUserId(sendOtpTrustedDeviceRequest.userId)
            .setChannel(sendOtpTrustedDeviceRequest.channel)
            .setDeviceId(sendOtpTrustedDeviceRequest.deviceId)
            .setDeviceModel(sendOtpTrustedDeviceRequest.deviceModel)
            .setManufacture(sendOtpTrustedDeviceRequest.manufacture)
            .setAppVersion(sendOtpTrustedDeviceRequest.appVersion)
            .build()

        return oltService.sendOtpTrustedDevice(request)
    }

    override suspend fun verifyOtpTrustedDevice(verifyOtpTrustedDeviceReq: VerifyOtpTrustedDeviceRequest): VerifyOtpResponse? {
        val request = VerifyOtpRequest.newBuilder()
            .setUserId(verifyOtpTrustedDeviceReq.userId)
            .setOtp(verifyOtpTrustedDeviceReq.otp)
            .setDeviceId(verifyOtpTrustedDeviceReq.deviceId)
            .build()

        return oltService.verifyOtpTrustedDevice(request)
    }

    override suspend fun deleteTrustedDevice(deleteTrustedDevice: DeleteTrustedDeviceRequest): DeleteDeviceResponse? {
        val request = DeleteDeviceRequest.newBuilder()
            .setUserId(deleteTrustedDevice.userId)
            .setSessionId(deleteTrustedDevice.sessionId)
            .setDeviceId(deleteTrustedDevice.deviceId)
            .build()

        return oltService.deleteTrustedDevice(request)
    }

    override suspend fun closeChannel() {
        oltService.closeChannel()
    }
}