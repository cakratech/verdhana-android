package com.bcasekuritas.mybest.app.feature.order.tabsell

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.MaxOrderByStockReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockPosRequest
import com.bcasekuritas.mybest.app.domain.interactors.GetMaxOrderByStockUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockPosUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.AccStockPosResponse
import com.bcasekuritas.rabbitmq.proto.bcas.MaxOrderByStockResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OrderSellViewModel @Inject constructor(
    private val getStockPosUseCase: GetStockPosUseCase,
    private val getMaxOrderByStockUseCase: GetMaxOrderByStockUseCase
): BaseViewModel(){
    val getStockPosResult = MutableLiveData<Resource<AccStockPosResponse?>>()
    val getMaxOrderByStockResult = MutableLiveData<Resource<MaxOrderByStockResponse?>>()

    fun getStockPos(userId: String, accno: String, sessionId: String, stockCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val stockPosRequest = StockPosRequest(userId, accno, sessionId, stockCode)

            getStockPosUseCase.invoke(stockPosRequest).collect() { res ->
                res.let {
                    getStockPosResult.postValue(it)
                }
            }
        }
    }

    fun getMaxOrder(userId: String, accNo: String, sessionId: String, stockCode: String,
                     price: Double, buyType: String, boardCode: String, relId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val maxOrderByStockReq = MaxOrderByStockReq(
                userId = userId,
                accNo = accNo,
                buySell = "S",
                stockCode = stockCode,
                price = price,
                buyType = buyType,
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
}