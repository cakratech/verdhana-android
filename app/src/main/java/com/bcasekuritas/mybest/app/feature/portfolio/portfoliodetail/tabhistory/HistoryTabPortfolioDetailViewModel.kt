package com.bcasekuritas.mybest.app.feature.portfolio.portfoliodetail.tabhistory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.PageRequest
import com.bcasekuritas.mybest.app.domain.dto.request.TradeListHistoryReq
import com.bcasekuritas.mybest.app.domain.interactors.GetTradeListHistoryUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryTabPortfolioDetailViewModel @Inject constructor(
    private val getOrderHistoryUseCase: GetTradeListHistoryUseCase
): BaseViewModel() {
    val getOrderHistoryResult = MutableLiveData<TradeListHistoryResponse?>()

    fun getOrderHistory(userId: String, accNo: String, sessionId: String, startDate: Long, endDate: Long, stockCode: String, page: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val pageRequest = PageRequest(page, 10)
            val orderHistoryReq = TradeListHistoryReq(userId, sessionId, accNo, startDate, endDate, pageRequest, stockCode)

            getOrderHistoryUseCase.invoke(orderHistoryReq).collect(){resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            getOrderHistoryResult.postValue(it.data)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

}