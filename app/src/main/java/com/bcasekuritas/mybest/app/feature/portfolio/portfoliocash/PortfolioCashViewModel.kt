package com.bcasekuritas.mybest.app.feature.portfolio.portfoliocash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.CashPosRequest
import com.bcasekuritas.mybest.app.domain.dto.request.SettlementSchedReq
import com.bcasekuritas.mybest.app.domain.interactors.GetCashPosUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSettlementSchedUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.proto.bcas.CIFCashPosResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SettlementScheduleResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioCashViewModel @Inject constructor(
    private val getCashPosUseCase: GetCashPosUseCase,
    private val getSettlementSchedUseCase: GetSettlementSchedUseCase,
    val imqConnectionListener: IMQConnectionListener
): BaseViewModel() {
    val getCashPosResult = MutableLiveData<CIFCashPosResponse?>()
    val getSettlementSchedResult = MutableLiveData<SettlementScheduleResponse?>()

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

    fun getSettlementSched(userId: String, accNo: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val settlementShedReq = SettlementSchedReq(userId, accNo,  sessionId)

            getSettlementSchedUseCase.invoke(settlementShedReq).collect() {resource ->
                resource.let {
                    when (resource) {
                        is Resource.Success -> {
                            getSettlementSchedResult.postValue(resource.data)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

}