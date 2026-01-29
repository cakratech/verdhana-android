package com.bcasekuritas.mybest.app.feature.security.changepassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.BiometricObject
import com.bcasekuritas.mybest.app.domain.dto.request.ChangePasswordReq
import com.bcasekuritas.mybest.app.domain.interactors.GetChangePasswordUsecase
import com.bcasekuritas.mybest.app.domain.interactors.InsertBiometricDaoUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.ChangePasswordResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val getChangePasswordUsecase: GetChangePasswordUsecase,
    private val insertTokenUseCase: InsertBiometricDaoUseCase,

    ) : BaseViewModel() {

    val getChangePasswordResult = MutableLiveData<Resource<ChangePasswordResponse?>>()

    fun getChangePassword(userId: String, oldPass: String, newPass: String, confirmPass: String) {

        viewModelScope.launch {
            val changePasswordReq = ChangePasswordReq(userId, oldPass, newPass, confirmPass)

            getChangePasswordResult.postValue(Resource.Loading)
            getChangePasswordUsecase.invoke(changePasswordReq).collect { resource ->
                resource.let {
                    getChangePasswordResult.postValue(it)
                }
            }
        }
    }

    fun insertToken(biometricObject: BiometricObject) {
        viewModelScope.launch {
            insertTokenUseCase.insertToken(biometricObject)
        }
    }
}