package com.bcasekuritas.mybest.app.feature.portfolio.stoplosstakeprofit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.AdvanceOrderListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawOrderRequest
import com.bcasekuritas.mybest.app.domain.interactors.GetAdvanceOrderInfoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetOrderListUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSessionPinDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SendWithdrawAdvancedOrderUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SendWithdrawUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.proto.bcas.AdvancedOrderInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StopLossTakeProfitViewModel @Inject constructor(
    private val getAdvanceOrderInfoUseCase: GetAdvanceOrderInfoUseCase,
    private val sendWithdrawUseCase: SendWithdrawAdvancedOrderUseCase,
    val imqConnectionListener: IMQConnectionListener
): BaseViewModel() {
    val getAdvanceOrderListResult = MutableLiveData<List<AdvancedOrderInfo>>()
    val isSessionExpired = MutableLiveData<Boolean>()
    var getSessionPinResult = MutableLiveData<Long?>()

    fun getAdvanceOrderList(userId: String, accNo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val advanceOrderListRequest = AdvanceOrderListRequest(userId, accNo, true)

            getAdvanceOrderInfoUseCase.invoke(advanceOrderListRequest).collect() {resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            when (it.data?.status) {
                                0 -> getAdvanceOrderListResult.postValue(it.data.advancedOrderInfoList)
                                2 -> isSessionExpired.postValue(true)
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun sendWithdraw(sendWithdraw: WithdrawOrderRequest){
        viewModelScope.launch(Dispatchers.IO) {
            sendWithdrawUseCase.sendWithdraw(sendWithdraw)
        }
    }

}