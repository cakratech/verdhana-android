package com.bcasekuritas.mybest.app.feature.security.menusecurity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.ChangePinReq
import com.bcasekuritas.mybest.app.domain.interactors.GetChangePinUsecase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.ChangePinResponse
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val getChangePinUsecase: GetChangePinUsecase
): BaseViewModel() {
    val getChangePinResult = MutableLiveData<Resource<ChangePinResponse?>>()

    fun getChangePassword(userId: String, oldPin: String, newPin: String, confirmPin: String) {

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

}