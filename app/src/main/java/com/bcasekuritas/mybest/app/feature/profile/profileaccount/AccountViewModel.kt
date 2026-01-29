package com.bcasekuritas.mybest.app.feature.profile.profileaccount

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.AccountInfoRequest
import com.bcasekuritas.mybest.app.domain.interactors.GetAccountInfoUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.CifDetailInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val getAccountInfoUseCase: GetAccountInfoUseCase
): BaseViewModel() {
    val clientInfoResult = MutableLiveData<CifDetailInfo>()

    fun getAccountInfo(userId: String, cifCode: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {

            val accountInfoRequest = AccountInfoRequest(userId, cifCode, 1, sessionId)

            getAccountInfoUseCase.invoke(accountInfoRequest).collect() {resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            clientInfoResult.postValue(it.data?.cifInfo)

                        } else -> {}
                    }
                }
            }

        }
    }
}