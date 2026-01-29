package com.bcasekuritas.mybest.app.feature.security.changepin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.SessionObject
import com.bcasekuritas.mybest.app.domain.dto.request.ChangePinReq
import com.bcasekuritas.mybest.app.domain.dto.request.ValidatePinReq
import com.bcasekuritas.mybest.app.domain.interactors.GetChangePinUsecase
import com.bcasekuritas.mybest.app.domain.interactors.GetValidatePinUseCase
import com.bcasekuritas.mybest.app.domain.interactors.InputPinUseCase
import com.bcasekuritas.mybest.app.domain.interactors.InsertSessionDaoUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.ChangePinResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ValidatePinResponse
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePinViewModel @Inject constructor(
    private val inputPinUseCase: InputPinUseCase,
    private val getValidatePinUseCase: GetValidatePinUseCase,
    private val getChangePinUsecase: GetChangePinUsecase,
    private val insertSessionDaoUseCase: InsertSessionDaoUseCase
) : BaseViewModel() {

    val inputPinResult = MutableLiveData<Resource<List<String>>>()
    val validatePinResult = MutableLiveData<Resource<ValidatePinResponse?>>()
    val getChangePinResult = MutableLiveData<Resource<ChangePinResponse?>>()

    fun inputPin(listPin: ArrayList<String>, pinInput: String) {
        viewModelScope.launch {
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
        viewModelScope.launch {
            getValidatePinUseCase.invoke(ValidatePinReq(userId,pinValue,sessionId,false)).collect(){ resource ->
                resource.let {
                    validatePinResult.postValue(it)
                }
            }
        }
    }

    fun insertSession(sessionObject: SessionObject) {
        viewModelScope.launch {
            insertSessionDaoUseCase.insertSessionDao(sessionObject)
        }
    }
}
