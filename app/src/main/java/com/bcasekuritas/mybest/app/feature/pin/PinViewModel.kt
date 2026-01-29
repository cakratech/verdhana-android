package com.bcasekuritas.mybest.app.feature.pin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.SessionObject
import com.bcasekuritas.mybest.app.domain.dto.request.ChangePinReq
import com.bcasekuritas.mybest.app.domain.dto.request.LogoutReq
import com.bcasekuritas.mybest.app.domain.dto.request.ValidatePinReq
import com.bcasekuritas.mybest.app.domain.interactors.DeleteSessionsDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetChangePinUsecase
import com.bcasekuritas.mybest.app.domain.interactors.GetLogoutUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetValidatePinUseCase
import com.bcasekuritas.mybest.app.domain.interactors.InputPinUseCase
import com.bcasekuritas.mybest.app.domain.interactors.InsertSessionDaoUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.ChangePinResponse
import com.bcasekuritas.rabbitmq.proto.bcas.LogoutResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ValidatePinResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinViewModel @Inject constructor(
    private val inputPinUseCase: InputPinUseCase,
    private val getValidatePinUseCase: GetValidatePinUseCase,
    private val getChangePinUsecase: GetChangePinUsecase,
    private val logoutUseCase: GetLogoutUseCase,
    private val insertSessionDaoUseCase: InsertSessionDaoUseCase,
    private val deleteSessions: DeleteSessionsDaoUseCase
) : BaseViewModel() {

    val inputPinResult = MutableLiveData<Resource<List<String>>>()
    val validatePinResult = MutableLiveData<Resource<ValidatePinResponse?>>()
    val getChangePinResult = MutableLiveData<Resource<ChangePinResponse?>>()
    val getLogoutResult = MutableLiveData<LogoutResponse?>()

    fun inputPin(listPin: ArrayList<String>, pinInput: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = inputPinUseCase.invoke(listPin, pinInput)
            when (res) {
                is Resource.Success -> {
                    inputPinResult.postValue(res)
                }

                else -> {

                }
            }
        }
    }

    fun getChangePin(userId: String, oldPin: String, newPin: String, confirmPin: String) {

        viewModelScope.launch {
            val changePinReq = ChangePinReq(userId, oldPin, newPin, confirmPin)

            getChangePinResult.postValue(Resource.Loading)
            getChangePinUsecase.invoke(changePinReq).collect { resource ->
                resource.let {
                    getChangePinResult.postValue(it)
                }
            }
        }
    }

    fun validatePin(userId: String, pinValue: String, sessionId: String){
        viewModelScope.launch(Dispatchers.IO) {
            getValidatePinUseCase.invoke(ValidatePinReq(userId,pinValue,sessionId,false)).collect(){resource ->
                resource.let {
                    validatePinResult.postValue(it)
                }
            }
        }
    }

    fun insertSession(sessionObject: SessionObject) {
        viewModelScope.launch(Dispatchers.IO) {
            insertSessionDaoUseCase.insertSessionDao(sessionObject)
        }
    }

    fun deleteSessionPin(){
        CoroutineScope(Dispatchers.IO).launch {
            deleteSessions.deleteSessionsDao()
        }
    }

    fun deleteSession(){
        CoroutineScope(Dispatchers.IO).launch {
            deleteSessions.deleteSessionsDao()
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
