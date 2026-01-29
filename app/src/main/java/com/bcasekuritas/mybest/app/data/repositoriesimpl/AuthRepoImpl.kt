package com.bcasekuritas.mybest.app.data.repositoriesimpl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.bcasekuritas.mybest.app.domain.repositories.AuthRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
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
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val remoteSource: AuthDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : AuthRepo {
    override suspend fun getLogin(logonRequest: LogonRequest): Flow<Resource<LogonResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getLogin(logonRequest), DataSource.REMOTE))
    }

    override suspend fun getLogout(logoutReq: LogoutReq): Flow<Resource<LogoutResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getLogout(logoutReq), DataSource.REMOTE))
    }

    override suspend fun getValidatePin(validatePin: ValidatePinReq): Flow<Resource<ValidatePinResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getValidatePin(validatePin), DataSource.REMOTE))
    }

    override suspend fun getChangePassword(changePasswordReq: ChangePasswordReq): Flow<Resource<ChangePasswordResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getChangePassword(changePasswordReq), DataSource.REMOTE))
    }

    override suspend fun getChangePin(changePinReq: ChangePinReq): Flow<Resource<ChangePinResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getChangePin(changePinReq), DataSource.REMOTE))
    }

    override suspend fun saveDeviceToken(saveDeviceTokenReq: SaveDeviceTokenReq?): Flow<Resource<SaveDeviceTokenResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.saveDeviceToken(saveDeviceTokenReq), DataSource.REMOTE))
    }

    override suspend fun validateSession(validateSessionReq: ValidateSessionReq): Flow<Resource<ValidateSessionResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getValidateSession(validateSessionReq), DataSource.REMOTE))
    }

    override suspend fun sendPing(userId: String) {
        remoteSource.sendPing(userId)
    }

    override suspend fun insertSession(sessionObject: SessionObject) {
        remoteSource.insertSession(sessionObject)
    }

    override suspend fun deleteSessions() {
        remoteSource.deleteSessions()
    }

    override suspend fun updateSessionPin(userId: String, sessionPin: String) {
        remoteSource.updateSessionPin(userId, sessionPin)
    }

    override suspend fun getSessionPin(userId: String): Flow<Resource<Long>> = flow {
        emit(Resource.Success(data = remoteSource.getSessionPin(userId), DataSource.REMOTE))
    }

    override suspend fun insertToken(biometricObject: BiometricObject) {
        remoteSource.insertToken(biometricObject)
    }

    override suspend fun updateToken(userId: String, token: String) {
        remoteSource.updateToken(userId, token)
    }

    override suspend fun getToken(userId: String): Flow<Resource<BiometricObject>> = flow {
        emit(Resource.Success(data = remoteSource.getToken(userId), DataSource.REMOTE))
    }

    override suspend fun deleteBiometric() {
        remoteSource.deleteBiometric()
    }

    override suspend fun deleteToken(userId: String) {
        remoteSource.deleteToken(userId)
    }

    override suspend fun startAppNotification(
        cakraListener: MQMessageListener<CakraMessage>?
    ) {
        remoteSource.startAppNotification(cakraListener)
    }

    override suspend fun subscribeAppNotification(routingKey: String?) {
        remoteSource.subscribeAppNotification(routingKey)
    }

    override suspend fun unsubscribeAppNotification(routingKey: String?) {
        remoteSource.unsubscribeAppNotification(routingKey)
    }

    override suspend fun stopAppNotification() {
        remoteSource.stopAppNotification()
    }

    private var _appNotificationLiveData = MutableLiveData<AppNotification>()

    override val appNotificationLiveData: LiveData<AppNotification>
        get() = _appNotificationLiveData

    override suspend fun startNewAppNotification() {
        remoteSource.startNewAppNotification {
            _appNotificationLiveData.postValue(it.appNotification)
        }
    }

    override suspend fun clearAppNotifLiveData() {
        _appNotificationLiveData = MutableLiveData<AppNotification>()
    }

    override suspend fun startAllConsumer(isFirstLaunch: Boolean): Flow<Resource<Boolean>> = flow {
        emit(Resource.Success(data = remoteSource.startAllConsumer(isFirstLaunch), DataSource.REMOTE))
    }

    override suspend fun getIpAddress(): Flow<Resource<String>> = flow {
        emit(Resource.Success(data = remoteSource.getIpAddress(), DataSource.REMOTE))
    }

    override suspend fun getTrustedDevice(trustedDeviceReq: TrustedDeviceReq): Flow<Resource<TrustedDeviceResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getTrustedDevice(trustedDeviceReq), DataSource.REMOTE))
    }

    override suspend fun sendOtpTrustedDevice(sendOtpTrustedDeviceRequest: SendOtpTrustedDeviceRequest): Flow<Resource<SendOtpResponse?>> = flow  {
        emit(Resource.Success(data = remoteSource.sendOtpTrustedDevice(sendOtpTrustedDeviceRequest), DataSource.REMOTE))
    }

    override suspend fun verifyOtpTrustedDevice(verifyOtpTrustedDeviceReq: VerifyOtpTrustedDeviceRequest): Flow<Resource<VerifyOtpResponse?>> = flow  {
        emit(Resource.Success(data = remoteSource.verifyOtpTrustedDevice(verifyOtpTrustedDeviceReq), DataSource.REMOTE))
    }

    override suspend fun deleteTrustedDevice(deleteTrustedDevice: DeleteTrustedDeviceRequest): Flow<Resource<DeleteDeviceResponse?>> = flow  {
        emit(Resource.Success(data = remoteSource.deleteTrustedDevice(deleteTrustedDevice), DataSource.REMOTE))
    }

    override suspend fun closeChannel() {
        remoteSource.closeChannel()
    }
}