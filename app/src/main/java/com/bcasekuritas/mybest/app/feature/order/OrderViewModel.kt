package com.bcasekuritas.mybest.app.feature.order

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.AccountObject
import com.bcasekuritas.mybest.app.data.entity.StockNotationObject
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.data.mapper.toTradeSummary
import com.bcasekuritas.mybest.app.domain.dto.request.AdvanceOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.AmendOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.AutoOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.LogoutReq
import com.bcasekuritas.mybest.app.domain.dto.request.MarketSessionReq
import com.bcasekuritas.mybest.app.domain.dto.request.MaxOrderByStockReq
import com.bcasekuritas.mybest.app.domain.dto.request.SendOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.SliceOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockOrderbookRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockPosRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.response.OrderBookSum
import com.bcasekuritas.mybest.app.domain.dto.response.TradeSummary
import com.bcasekuritas.mybest.app.domain.interactors.DeleteSessionsDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetAccountInfoDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetIpAddressUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetLogoutUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetMarketSessionUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetMaxOrderByStockUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetNotationByStockCodeDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSessionPinDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockDetailUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockOrderBookUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockPosUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SearchStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SendAdvOrderBuyUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SendAmendUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SendAutoOrderUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SendOrderUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SendSliceOrderUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SetListenerOrderBookUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SetListenerTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeOrderBookListUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnSubscribeOrderBookListUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnSubscribeTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.converter.tryDeserialize
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.message.MQMessageEvent
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.bcas.AccStockPosResponse
import com.bcasekuritas.rabbitmq.proto.bcas.LogoutResponse
import com.bcasekuritas.rabbitmq.proto.bcas.MarketSessionInfo
import com.bcasekuritas.rabbitmq.proto.bcas.MaxOrderByStockResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.datafeed.MIType
import com.bcasekuritas.rabbitmq.proto.datafeed.OrderbookSummary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val getOrderBookUseCase: GetStockOrderBookUseCase,
    private val setListenerOrderBookUseCase: SetListenerOrderBookUseCase,
    private val subscribeOrderbookListUseCase: SubscribeOrderBookListUseCase,
    private val unSubscribeOrderbookListUseCase: UnSubscribeOrderBookListUseCase,
    private val sendAmendUseCase: SendAmendUseCase,
    private val sendOrderUseCase: SendOrderUseCase,
    private val sendSliceOrderUseCase: SendSliceOrderUseCase,
    private val getSessionPin: GetSessionPinDaoUseCase,
    private val searchStockParamDaoUseCase: SearchStockParamDaoUseCase,
    private val getStockParamDaoUseCase: GetStockParamDaoUseCase,
    private val getAccountInfoDaoUseCase: GetAccountInfoDaoUseCase,
    private val getStockPosUseCase: GetStockPosUseCase,
    private val getStockDetailUseCase: GetStockDetailUseCase,
    private val getMaxOrderByStockUseCase: GetMaxOrderByStockUseCase,
    private val sendAdvOrderBuyUseCase: SendAdvOrderBuyUseCase,
    private val sendAutoOrderUseCase: SendAutoOrderUseCase,
    private val getNotationByStockCodeDaoUseCase: GetNotationByStockCodeDaoUseCase,
    private val getMarketSessionUseCase: GetMarketSessionUseCase,
    val imqConnectionListener: IMQConnectionListener,
    private val setListenerTradeSumUseCase: SetListenerTradeSumUseCase,
    private val subscribeTradeSumUseCase: SubscribeTradeSumUseCase,
    private val unSubscribeTradeSumUseCase: UnSubscribeTradeSumUseCase,
    private val logoutUseCase: GetLogoutUseCase,
    private val deleteSessions: DeleteSessionsDaoUseCase,
    private val getIpAddressUseCase: GetIpAddressUseCase
) : BaseViewModel() {

    val getStockPosResult = MutableLiveData<Resource<AccStockPosResponse?>>()
    val getStockOrderbookResult = MutableLiveData<List<OrderbookSummary?>>()
    val getPinSessionResult = MutableLiveData<Long?>()
    var getAllStockParamResult = MutableLiveData<List<StockParamObject?>>()
    var getStockParamResult = MutableLiveData<StockParamObject?>()
    var getAccountInfoResult = MutableLiveData<AccountObject?>()
    var getStockDetailResult = MutableLiveData<TradeSummary?>()
    val getMaxOrderByStockResult = MutableLiveData<Resource<MaxOrderByStockResponse?>>()
    var getStockNotationResult = MutableLiveData<List<StockNotationObject?>>()
    val getSubscribeTradeSummary = MutableLiveData<TradeSummary?>()
    val getLogoutResult = MutableLiveData<LogoutResponse?>()
    val getOrderBookSum = MutableLiveData<OrderBookSum>()
    val getIpAddressResult = MutableLiveData<String>()

    private var secCode = ""

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

    fun getStockOrderbook(userId: String, userSession: String, stockCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val stockCodeList = listOf<String>(stockCode)
            secCode = stockCode

            val stockOrderbookRequest =
                StockOrderbookRequest(userId, userSession, "RG", stockCodeList)

            getOrderBookUseCase.invoke(stockOrderbookRequest).collect() { resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            if (!it.data.isNullOrEmpty()) {
                                it.data[0]?.let { item -> subscribeOrderbook(item.secCode) }
                                getStockOrderbookResult.postValue(it.data.filterNotNull())
                                getOrderBookSum.postValue(
                                    OrderBookSum(
                                        it.data[0]?.totalBidQtyM ?: 0,
                                        it.data[0]?.totalOfferQtyM ?: 0
                                    )
                                )
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    fun getAllStockParam(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
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

    fun sendOrder(sendOrder: SendOrderReq) {
        viewModelScope.launch(Dispatchers.IO) {
            sendOrderUseCase.sendOrder(sendOrder)
        }
    }

    fun sendAdvOrder(advOrder: AdvanceOrderRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            sendAdvOrderBuyUseCase.sendAdvOrderBuy(advOrder)
        }
    }

    fun sendSliceOrder(sliceOrderRequest: SliceOrderRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            sendSliceOrderUseCase.SendSliceOrder(sliceOrderRequest)
        }
    }

    fun sendAutoOrder(autoOrderRequest: AutoOrderRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            sendAutoOrderUseCase.sendAutoOrder(autoOrderRequest)
        }
    }

    fun sendAmend(sendAmend: AmendOrderRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            sendAmendUseCase.SendAmend(sendAmend)
        }
    }

    fun setListenerOrderBookTradeSum() {
        viewModelScope.launch(Dispatchers.IO) {
            setListenerTradeSumUseCase.setListener(miListener)
            setListenerOrderBookUseCase.setListenerOrderBook(miListener)
        }
    }

    private fun subscribeOrderbook(secCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeOrderbookListUseCase.subscribeOrderBook("RG.$secCode")
        }
    }

    fun unSubscribeOrderbook(secCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            unSubscribeOrderbookListUseCase.unSubscribeOrderBook("RG.$secCode")
        }
    }

    fun getSessionPin(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getSessionPin.invoke(userId).collect() { resource ->
                when (resource){
                    is Resource.Success -> {
                        getPinSessionResult.postValue(resource.data)
                    }
                    else -> {}
                }
            }
        }
    }

    fun getStockParam(stockCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getStockParamDaoUseCase(stockCode).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { data ->
                            withContext(Dispatchers.Main) {
                                getStockParamResult.postValue(data)
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

    fun getAccountInfo(accNo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getAccountInfoDaoUseCase(accNo).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { data ->
                            withContext(Dispatchers.Main) {
                                getAccountInfoResult.postValue(data)
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

    fun getMaxOrder(userId: String, accNo: String, stockCode: String,
                    price: Double, buyType: String, boardCode: String, buySell: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val maxOrderByStockReq = MaxOrderByStockReq(
                userId = userId,
                accNo = accNo,
                buySell = buySell,
                stockCode = stockCode,
                price = price,
                buyType = buyType,
                boardCode = boardCode,
                sessionId = sessionId)

            getMaxOrderByStockUseCase.invoke(maxOrderByStockReq).collect() {resource ->
                resource.let {
                    withContext(Dispatchers.Main) {
                        getMaxOrderByStockResult.postValue(it)
                    }
                }
            }
        }
    }

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

    fun getStockNotation(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getNotationByStockCodeDaoUseCase(value).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { data ->
                            getStockNotationResult.postValue(data)
                            Timber.d("notasi : $data")
                        }
                    }

                    else -> {
                        // Handle other cases if needed
                    }
                }
            }
        }
    }

    val getMarketSessionResult = MutableLiveData<MarketSessionInfo>()

    fun getMarketSession(userId: String){
        viewModelScope.launch(Dispatchers.IO) {
            val marketSessionReq = MarketSessionReq(userId)

            getMarketSessionUseCase.invoke(marketSessionReq).collect(){ res ->
                when (res) {
                    is Resource.Success -> {
                        getMarketSessionResult.postValue(res.data?.marketSessionInfo)
                    }
                    else -> {

                    }
                }
            }
        }
    }

    fun getStockDetail(userId: String, userSession: String, stockCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val stockCodeReq = StockWatchListRequest(userId, userSession, "RG", listOf(stockCode))

            getStockDetailUseCase.invoke(stockCodeReq).collect() { resource ->
                resource.let { res ->
                    when (res) {
                        is Resource.Success -> {
                            if (res.data?.curMsgInfoCount != 0) {
                                val data = res.data?.curMsgInfoList?.toTradeSummary()?.get(0)
                                if (data != null) {
                                    getStockDetailResult.postValue(
                                        TradeSummary(
                                            secCode = data.secCode,
                                            bestOfferPrice = data.bestOfferPrice,
                                            bestBidPrice = data.bestBidPrice,
                                            last = data.last,
                                            close = data.close,
                                            avgPrice = data.avgPrice,
                                            change = data.change,
                                            changePct = data.changePct
                                        )
                                    )
                                    subscribeTradeSummary(stockCode)
                                }
                            } else {
                                getStockDetailResult.postValue(TradeSummary(
                                    secCode = stockCode
                                ))
                            }

                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun subscribeTradeSummary(routingKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            routingKey.map { subscribeTradeSumUseCase.subscribe("RG.$routingKey") }
        }
    }

    fun unSubscribeTradeSummary(routingKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            unSubscribeTradeSumUseCase.unSubscribe("RG.$routingKey")
        }
    }

    fun getLogout(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val logoutRequest = LogoutReq(userId, sessionId)

            logoutUseCase.invoke(logoutRequest).collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        getLogoutResult.postValue(resource.data)
                    }
                    else -> {}
                }
            }
        }
    }

    fun deleteSession(){
        CoroutineScope(Dispatchers.IO).launch {
            deleteSessions.deleteSessionsDao()
        }
    }

    private val miListener = MQMessageListener<MIMessage> { result ->
        viewModelScope.launch(Dispatchers.IO) {
            val parsedObject = result.protoMsg
            if (parsedObject.type == MIType.TRADE_SUMMARY) {
                Timber.tag("tradeSumRealtime").d( "get realtime data")
                val stockSummary = parsedObject.tradeSummary
                val stockCode = stockSummary.secCode

                if (secCode == stockCode) {
                    withContext(Dispatchers.Main) {
                        getSubscribeTradeSummary.postValue(
                            TradeSummary(
                                last = stockSummary.last,
                                change = stockSummary.change,
                                changePct = stockSummary.changePct,
                                close = stockSummary.close
                            )
                        )
                    }
                }
            }

            if (parsedObject.type == MIType.ORDERBOOK_SUMMARY_COMPACT) {
                Timber.tag("orderBookRealtime").d("get realtime data")
                val orderbookSummaryProto = parsedObject.orderbookSummary
                if (secCode == orderbookSummaryProto.secCode) {
                    withContext(Dispatchers.Main) {
                        getStockOrderbookResult.postValue(arrayListOf(orderbookSummaryProto))
                        getOrderBookSum.postValue(
                            OrderBookSum(
                                orderbookSummaryProto.totalBidQtyM,
                                orderbookSummaryProto.totalOfferQtyM
                            )
                        )
                    }
                }
            }
        }
    }
}
