package com.bcasekuritas.mybest.app.feature.portfolio.taborders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.domain.dto.request.AdvanceOrderListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.LogoutReq
import com.bcasekuritas.mybest.app.domain.dto.request.OrderListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.TradeListReq
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioOrderItem
import com.bcasekuritas.mybest.app.domain.interactors.DeleteSessionsDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetAdvanceOrderInfoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetIpAddressUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetListStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetLogoutUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetOrderListUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSessionPinOrderListDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetTradeListUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SearchStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SendWithdrawAdvancedOrderUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SendWithdrawUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.proto.bcas.AdvancedOrderInfo
import com.bcasekuritas.rabbitmq.proto.bcas.LogoutResponse
import com.bcasekuritas.rabbitmq.proto.bcas.OrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TradeInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OrdersTabPortfolioViewModel @Inject constructor(
    private val getAdvanceOrderInfoUseCase: GetAdvanceOrderInfoUseCase,
    private val getOrderListUseCase: GetOrderListUseCase,
    private val sendWithdrawUseCase: SendWithdrawUseCase,
    private val getSessionPinWithdraw: GetSessionPinOrderListDaoUseCase,
    private val getTradeListUseCase: GetTradeListUseCase,
    private val getListStockParamDaoUseCase: GetListStockParamDaoUseCase,
    private val searchStockParamDaoUseCase: SearchStockParamDaoUseCase,
    private val sendWithdrawGtcUseCase: SendWithdrawAdvancedOrderUseCase,
    val imqConnectionListener: IMQConnectionListener,
    private val logoutUseCase: GetLogoutUseCase,
    private val deleteSessions: DeleteSessionsDaoUseCase,
    private val getIpAddressUseCase: GetIpAddressUseCase
) : BaseViewModel() {

    val getAdvanceOrderListResult = MutableLiveData<List<AdvancedOrderInfo>>()
    val getOrderListResult = MutableLiveData<OrderListResponse?>()
    var getSessionPinWithdrawResult = MutableLiveData<Long?>()
    val getTradeListResult = MutableLiveData<List<TradeInfo>>()
    val getOrderListResults = MutableLiveData<List<PortfolioOrderItem>?>()
    val isOrderListEmpty = MutableLiveData<Boolean>()
    var getAllStockParamResult = MutableLiveData<List<StockParamObject?>>()
    val getLogoutResult = MutableLiveData<LogoutResponse?>()
    val getIpAddressResult = MutableLiveData<String>()

    private val stockDataMapper = mutableMapOf<String, PortfolioOrderItem>()

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

    fun getOrderList(userId: String, accNo: String, sessionId: String, includeTrade: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val orderListRequest = OrderListRequest(userId, accNo, sessionId, includeTrade)

            getOrderListUseCase.invoke(orderListRequest).collect(){resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            stockDataMapper.clear()
                            if (it.data != null) {
                                if (it.data.ordersList?.size != 0) {
                                    val listStockCode = arrayListOf<String>()
                                    it.data.ordersList?.map { data ->
                                        var status = data.ostatus
                                        var matchQty = data.currMqty
                                        if (status == "A") {
                                            status = if (data.currMqty > 0) "Partial Amended" else "A"
                                        }
                                        if (status == "P") {
                                            status = if (data.currMqty > 0) "P" else "O"
                                            matchQty = if (data.currMqty > 0) data.currMqty else data.accMqty
                                        }
                                        val price = if (data.ordertype == "5") data.lastMatchPrice else data.oprice
                                        stockDataMapper[data.odId] = PortfolioOrderItem(
                                            data.odId,
                                            data.exordid,
                                            data.odTime,
                                            status,
                                            data.bs,
                                            data.ordertype,
                                            data.stockcode,
                                            data.remark,
                                            price,
                                            data.oqty,
                                            matchQty,
                                            timeInForce = data.timeInForce,
                                            ordPeriod = data.ordperiod.toLong(),
                                            ordValue = data.ordValue,
                                            mValue = data.mvalue,
                                            channel = data.channel,
                                            accMQty = data.accMqty,
                                            isGtOrder = data.gtOrder,
                                            advOrderId = data.advOrdId,
                                            isWdForToday = data.wdForToday,
                                            channelForFee = data.channelForFee,
                                            matchPrice = data.lastMatchPrice
                                        )
                                        listStockCode.add(data.stockcode)
                                    }
                                    getListStockParam(listStockCode.distinct())
                                } else {
                                  isOrderListEmpty.postValue(true)
                                }
                            }
                        } else -> {}
                    }
                }
            }
        }
    }

    fun getTradeList(userId: String, accNo: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val tradeListReq = TradeListReq(userId, sessionId, accNo)

            getTradeListUseCase.invoke(tradeListReq).collect() { resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            if (it.data != null) {
                                getTradeListResult.postValue(it.data.tradeInfoList)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    fun getAdvanceOrderList(userId: String, accNo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val advanceOrderListRequest = AdvanceOrderListRequest(userId, accNo, false)

            getAdvanceOrderInfoUseCase.invoke(advanceOrderListRequest).collect() { resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            when (it.data?.status) {
                                0 -> getAdvanceOrderListResult.postValue(it.data.advancedOrderInfoList)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    fun sendWithdraw(sendWithdraw: WithdrawOrderRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            sendWithdrawUseCase.sendWithdraw(sendWithdraw)
        }
    }

    fun sendWithdrawGtc(sendWithdraw: WithdrawOrderRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            sendWithdrawGtcUseCase.sendWithdraw(sendWithdraw)
        }
    }

    fun getSessionPin(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            getSessionPinWithdraw.invoke(userId).collect() { resource ->
                when (resource) {
                    is Resource.Success -> {
                        getSessionPinWithdrawResult.postValue(resource.data)
                    }

                    else -> {}
                }
            }
        }
    }

    fun clearSessionPin() {
        getSessionPinWithdrawResult.value = null
    }

    private fun getListStockParam(value: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            getListStockParamDaoUseCase(value).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { data ->
                            try {
                                data.forEach { item ->
                                    val listNotation = item?.stockNotation?.map { it.notation }
                                    stockDataMapper.values.forEach { stockItem ->
                                        if (stockItem.stockCode == item?.stockParam?.stockCode) {
                                            stockDataMapper[stockItem.orderId]?.let { mapItem ->
                                                if (listNotation != null) {
                                                    mapItem.notation = listNotation.joinToString()
                                                }
                                            }
                                        }
                                    }
                                }

                                val safeList = ArrayList(stockDataMapper.values.toList()) // Ensure it's a separate list
                                getOrderListResults.postValue(safeList)
                            } catch (e: Exception){
                                Timber.tag("Orders Tab Viewmodel").d(e)
                            }

                        }

                    }

                    else -> {
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

    fun getLogout(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val logoutRequest = LogoutReq(userId, sessionId)

            logoutUseCase.invoke(logoutRequest).collect() { resource ->
                when (resource) {
                    is Resource.Success -> {
                        getLogoutResult.postValue(resource.data)
                    }

                    else -> {}
                }
            }
        }
    }

    fun deleteSession() {
        CoroutineScope(Dispatchers.IO).launch {
            deleteSessions.deleteSessionsDao()
        }
    }

}