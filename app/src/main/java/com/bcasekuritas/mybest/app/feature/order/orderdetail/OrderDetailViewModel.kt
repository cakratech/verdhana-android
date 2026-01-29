package com.bcasekuritas.mybest.app.feature.order.orderdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.StockWithNotationObject
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawOrderRequest
import com.bcasekuritas.mybest.app.domain.interactors.GetIpAddressUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetListStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SendWithdrawAdvancedOrderUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SendWithdrawUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    val sendWithdrawUseCase: SendWithdrawUseCase,
    private val sendWithdrawAdvancedOrderUseCase: SendWithdrawAdvancedOrderUseCase,
    private val getStockParamDaoUseCase: GetListStockParamDaoUseCase,
    private val getIpAddressUseCase: GetIpAddressUseCase
): BaseViewModel() {

    var getStockParamResult = MutableLiveData<StockWithNotationObject?>()
    val getIpAddressResult = MutableLiveData<String>()

    fun getIpAddress() {
        viewModelScope.launch(Dispatchers.IO) {
            getIpAddressUseCase.invoke().collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        getIpAddressResult.postValue(resource.data?:"")
                    }
                    else -> {}
                }
            }
        }
    }

    fun sendWithdraw(sendWithdraw: WithdrawOrderRequest){
        viewModelScope.launch(Dispatchers.IO) {
            sendWithdrawUseCase.sendWithdraw(sendWithdraw)
        }
    }

    fun sendWithdrawAdvancedOrder(sendWithdraw: WithdrawOrderRequest){
        viewModelScope.launch(Dispatchers.IO) {
            sendWithdrawAdvancedOrderUseCase.sendWithdraw(sendWithdraw)
        }
    }

    fun getStockParam(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getStockParamDaoUseCase(listOf(value)).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { data ->
                            withContext(Dispatchers.Main){
                                if (resource.data.isNotEmpty()) {
                                    getStockParamResult.postValue(data.get(0))
                                }
                            }
                        }
                    }

                    else -> {
                        // Handle other cases if needed
                    }
                }
            }
        }
    }



}