package com.bcasekuritas.mybest.app.feature.portfolio.tabhistory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.domain.dto.request.PageRequest
import com.bcasekuritas.mybest.app.domain.dto.request.TradeListHistoryDetailReq
import com.bcasekuritas.mybest.app.domain.dto.request.TradeListHistoryReq
import com.bcasekuritas.mybest.app.domain.dto.response.TradeListInfo
import com.bcasekuritas.mybest.app.domain.interactors.GetTradeListHistoryGroupDetailUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetTradeListHistoryGroupUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetTradeListHistoryUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SearchStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryGroupDetail
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryTabPortfolioViewModel @Inject constructor(
    private val getOrderHistoryUseCase: GetTradeListHistoryUseCase,
    private val searchStockParamDaoUseCase: SearchStockParamDaoUseCase,
    private val getTradeListHistoryGroupUseCase: GetTradeListHistoryGroupUseCase,
    private val getTradeListHistoryGroupDetailUseCase: GetTradeListHistoryGroupDetailUseCase,
    val imqConnectionListener: IMQConnectionListener
): BaseViewModel() {

    val getOrderHistoryResult = MutableLiveData<TradeListHistoryResponse?>()
    val getOrderHistoryResults = MutableLiveData<List<TradeListInfo>?>()
    var getAllStockParamResult = MutableLiveData<List<StockParamObject?>>()
    var getRealizeGainLossResults = MutableLiveData<Double>()

    fun clearOrderHistory() {
        viewModelScope.launch {
            getOrderHistoryResults.postValue(null)
        }
    }

    fun getTradeListHistoryGroup(userId: String, accNo: String, sessionId: String, startDate: Long, endDate: Long, page: Int, stockCode: String) {
        viewModelScope.launch {
            val pageRequest = PageRequest(page, 10)
            val orderHistoryReq = TradeListHistoryReq(userId, sessionId, accNo, startDate, endDate, pageRequest, stockCode)

            getTradeListHistoryGroupUseCase.invoke(orderHistoryReq).collect() {resource ->
                resource.let { it ->
                    when (it) {
                        is Resource.Success -> {
                            val data = it.data?.tradeListHistoryGroupList.orEmpty()
                            getRealizeGainLossResults.postValue(it.data?.realizeGainLoss)

                            // 1. Get only items with children to fetch their details
                            val getDetail = data
                                .map { parent ->
                                    val parentId = parent.exchordid
                                    val price = parent.mprice
                                    async(Dispatchers.IO) {
                                        val childDetails = getTradeListDetailAwait(userId, accNo, sessionId, parentId, price)
                                        Pair(parentId, price) to childDetails.map { child ->
                                            TradeListInfo(
                                                orderId = child.tdId,
                                                idxOrderId = child.exchordid,
                                                time = child.mtime.toDouble(),
                                                status = "M",
                                                buySell = child.bs,
                                                orderType = child.bs,
                                                timeInForce = "0",
                                                stockCode = child.stockcode,
                                                price = child.mprice,
                                                orderQty = child.mqty,
                                                matchQty = child.mqty,
                                                ordValue = child.mqty * child.mprice,
                                                mValue = child.mqty * child.mprice,
                                                fee = child.fee
                                            )
                                        }
                                    }
                                }

                            val detailsResult = getDetail.awaitAll().toMap()

                            // 2. Final list of TradeListInfo
                            val tradeList = data.map { item ->
                                val key = Pair(item.exchordid, item.mprice)
                                val children = detailsResult[key].orEmpty()

                                TradeListInfo(
                                    orderId = if (children.isNotEmpty()) children[0].orderId else item.exchordid,
                                    idxOrderId = item.exchordid,
                                    time = item.mtime.toDouble(),
                                    status = "M",
                                    buySell = item.bs,
                                    orderType = item.bs,
                                    timeInForce = "0",
                                    stockCode = item.stockcode,
                                    price = item.mprice,
                                    orderQty = item.mqty,
                                    matchQty = item.mqty,
                                    ordValue = item.mqty * item.mprice,
                                    mValue = item.mqty * item.mprice,
                                    fee = if (children.isNotEmpty()) children[0].fee else 0.0,
                                    listTradeInfo = children
                                )
                            }

                            getOrderHistoryResults.postValue(tradeList)

                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private suspend fun getTradeListDetailAwait(userId: String, accNo: String, sessionId: String, exchordId: String, price: Double): List<TradeListHistoryGroupDetail> {
        val request = TradeListHistoryDetailReq(userId, sessionId, accNo, exchordId, price)

        return getTradeListHistoryGroupDetailUseCase.invoke(request)
            .first { it is Resource.Success } // Only wait until we get a result
            .let { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.tradeListHistoryGroupDetailList.orEmpty()
                    }
                    else -> emptyList()
                }
            }
    }

    fun getOrderHistory(userId: String, accNo: String, sessionId: String, startDate: Long, endDate: Long, stockCode: String, page: Int) {
        viewModelScope.launch {
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

    fun getAllStockParam(value: String) {
        viewModelScope.launch {
            val resultList = mutableListOf<StockParamObject?>()
            searchStockParamDaoUseCase(value).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { data ->
                            resultList.addAll(data)
                            getAllStockParamResult.postValue(resultList.toList())
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