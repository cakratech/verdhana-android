package com.bcasekuritas.mybest.app.feature.rdn.withdraw

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.AccountInfoRequest
import com.bcasekuritas.mybest.app.domain.dto.request.CashPosRequest
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawCashReq
import com.bcasekuritas.mybest.app.domain.interactors.GetAccountInfoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetCashPosUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetWithdrawCashUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.common.getRandomString
import com.bcasekuritas.rabbitmq.proto.bcas.CIFCashPosResponse
import com.bcasekuritas.rabbitmq.proto.bcas.CifDetailInfo
import com.bcasekuritas.rabbitmq.proto.bcas.WithdrawCashResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WithdrawViewModel @Inject constructor(
    private val getWithdrawCashUseCase: GetWithdrawCashUseCase,
    private val getAccountInfoUseCase: GetAccountInfoUseCase,
    private val getCashPosUseCase: GetCashPosUseCase,
): BaseViewModel() {

    val withdrawCashResult = MutableLiveData<WithdrawCashResponse?>()
    val clientInfoResult = MutableLiveData<CifDetailInfo>()
    val getCashPosResult = MutableLiveData<CIFCashPosResponse?>()

    fun getCashPos(userId: String, cifCode: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val cashPosRequest = CashPosRequest(userId, cifCode, 1, sessionId)

            getCashPosUseCase.invoke(cashPosRequest).collect() {resource ->
                resource.let {
                    when (resource) {
                        is Resource.Success -> {
                            getCashPosResult.postValue(resource.data)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun sendWithdrawCash(userId: String, sessionId: String, accNo: String, amount: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = WithdrawCashReq(userId, sessionId, accNo, amount, "", getRandomString())

            getWithdrawCashUseCase.invoke(request).collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        withdrawCashResult.postValue(resource.data)
                    }
                    else -> {}
                }
            }
        }
    }

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