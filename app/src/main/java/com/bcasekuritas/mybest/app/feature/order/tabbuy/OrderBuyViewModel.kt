package com.bcasekuritas.mybest.app.feature.order.tabbuy

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.MaxOrderByStockReq
import com.bcasekuritas.mybest.app.domain.dto.request.SessionRequest
import com.bcasekuritas.mybest.app.domain.interactors.GetMaxOrderByStockForBuyingLimitUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetMaxOrderByStockUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSessionPinDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSimplePortfolioUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.event.SingleLiveEvent
import com.bcasekuritas.rabbitmq.proto.bcas.MaxOrderByStockResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimplePortofolioResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class OrderBuyViewModel @Inject constructor(
    private val getMaxOrderByStockUseCase: GetMaxOrderByStockUseCase,
    private val getMaxOrderByStockBuyLimitUseCase: GetMaxOrderByStockForBuyingLimitUseCase,
): BaseViewModel(){
    val getMaxOrderByStockResult = MutableLiveData<Resource<MaxOrderByStockResponse?>>()
    val getMaxOrderByStockBuyLimitResult = MutableLiveData<Resource<MaxOrderByStockResponse?>>()

    fun getMaxOrder(userId: String, accNo: String, sessionId: String, stockCode: String,
                    price: Double, compare: String, boardCode: String, relId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val maxOrderByStockReq = MaxOrderByStockReq(
                userId = userId,
                accNo = accNo,
                buySell = "B",
                stockCode = stockCode,
                price = price,
                buyType = compare,
                boardCode = boardCode,
                sessionId = sessionId,
                relId = relId
            )

            getMaxOrderByStockUseCase.invoke(maxOrderByStockReq).collect() {resource ->
                resource.let {
                    withContext(Dispatchers.Main) {
                        getMaxOrderByStockResult.postValue(it)
                    }
                }
            }
        }
    }

    fun getMaxOrderBuyLimit(userId: String, accNo: String, sessionId: String, stockCode: String,
                    price: Double, boardCode: String, relId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val maxOrderByStockReq = MaxOrderByStockReq(
                userId = userId,
                accNo = accNo,
                buySell = "B",
                stockCode = stockCode,
                price = price,
                buyType = "L",
                boardCode = boardCode,
                sessionId = sessionId,
                relId = relId
            )

            getMaxOrderByStockBuyLimitUseCase.invoke(maxOrderByStockReq).collect() {resource ->
                resource.let {
                    withContext(Dispatchers.Main) {
                        getMaxOrderByStockBuyLimitResult.postValue(it)
                    }
                }
            }
        }
    }
}