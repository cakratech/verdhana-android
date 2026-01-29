package com.bcasekuritas.mybest.app.feature.stockdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.mapper.toTradeSummary
import com.bcasekuritas.mybest.app.domain.dto.request.StockOrderbookRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.response.OrderBookSum
import com.bcasekuritas.mybest.app.domain.dto.response.TradeSummary
import com.bcasekuritas.mybest.app.domain.interactors.GetStockDetailUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockOrderBookUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SetListenerOrderBookUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SetListenerTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeOrderBookListUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnSubscribeOrderBookListUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnSubscribeTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.event.SingleLiveEvent
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.datafeed.MIType
import com.bcasekuritas.rabbitmq.proto.datafeed.OrderbookSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StockDetailSharedViewModel  @Inject constructor(
    private val getOrderBookUseCase: GetStockOrderBookUseCase,
    private val setListenerOrderBookUseCase: SetListenerOrderBookUseCase,
    private val subscribeOrderbookListUseCase: SubscribeOrderBookListUseCase,
    private val unSubscribeOrderbookListUseCase: UnSubscribeOrderBookListUseCase,
    private val getStockDetailUseCase: GetStockDetailUseCase,
    private val setListenerTradeSumUseCase: SetListenerTradeSumUseCase,
    private val subscribeTradeSumUseCase: SubscribeTradeSumUseCase,
    private val unSubscribeTradeSumUseCase: UnSubscribeTradeSumUseCase
): BaseViewModel() {

    private val _getStockOrderbookResult = MutableLiveData<OrderbookSummary?>()
    private val _isStockCodeChangeResult = MutableLiveData<Boolean?>()
    private val _isRefreshFragmentResult = MutableLiveData<Boolean?>()
    private val _isHideLoadingResult = MutableLiveData<Boolean?>()
    private var secCode = ""
    private var isRefresh = false
    private val _getPopUpSuccessPriceAlert = MutableLiveData<Boolean?>()
    private val _getStockDetailSummary = MutableLiveData<TradeSummary>()
    private val _getOrderBookSum = MutableLiveData<OrderBookSum>()
    private val _chipNewsOnClick = SingleLiveEvent<String>()

    val getChipNewsOnClick: SingleLiveEvent<String>
        get() = _chipNewsOnClick

    fun setChipNewsOnClick(stockCode: String) {
        viewModelScope.launch {
            _chipNewsOnClick.value = stockCode
        }
    }

    val getOrderBookSum: MutableLiveData<OrderBookSum>
        get() =_getOrderBookSum

    val getStockDetailSummary: MutableLiveData<TradeSummary>
        get() = _getStockDetailSummary

    val getStockOrderbookResult: MutableLiveData<OrderbookSummary?>
        get() = _getStockOrderbookResult

    val getStockCodeChangeResult: MutableLiveData<Boolean?>
        get() = _isStockCodeChangeResult

    val getRefreshFragmentResult: MutableLiveData<Boolean?>
        get() = _isRefreshFragmentResult

    val getHideLoadingResult: MutableLiveData<Boolean?>
        get() = _isHideLoadingResult

    val getPopUpSuccessPriceAlert: MutableLiveData<Boolean?>
        get() = _getPopUpSuccessPriceAlert

    fun setStockCodeChange(value: Boolean){
        viewModelScope.launch {
            _isStockCodeChangeResult.value = value
        }
    }

    fun setOnRefresh(value: Boolean) {
        viewModelScope.launch {
            isRefresh = value
            _isRefreshFragmentResult.value = value
            isRefresh = false
        }
    }

    fun getStockOrderbook(userId: String, userSession: String, stockCode: String) {
        viewModelScope.launch {
            val stockCodeList = listOf<String>(stockCode)
            secCode = stockCode

            val stockOrderbookRequest = StockOrderbookRequest(userId,userSession, "RG", stockCodeList)

            getOrderBookUseCase.invoke(stockOrderbookRequest).collect(){resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            if (!it.data.isNullOrEmpty()) {
                                _getStockOrderbookResult.postValue(it.data[0])
                                _getOrderBookSum.postValue(OrderBookSum(
                                    it.data[0]?.totalBidQtyM ?: 0L,
                                    it.data[0]?.totalOfferQtyM ?: 0L
                                ))
                            }else{
                                withContext(Dispatchers.Main) {
                                    _isHideLoadingResult.value = true
                                }
                            }
                            subscribeOrderbook(stockCode)
                        }

                        else -> {
                            withContext(Dispatchers.Main) {
                                _isHideLoadingResult.value = true
                            }
                        }
                    }
                }
            }

            withContext(Dispatchers.Main) {
                _isHideLoadingResult.value = true
            }
        }
    }

    fun getStockDetailSummary(userId: String, userSession: String, stockCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val stockCodeList = listOf<String>(stockCode)
            val stockCodeReq = StockWatchListRequest(userId,userSession, "RG", stockCodeList)

            getStockDetailUseCase.invoke(stockCodeReq).collect() { resource ->
                resource.let {res ->
                    when (res) {
                        is Resource.Success -> {
                            if (res.data != null) {
                                when (res.data.status) {
                                    0 -> {
                                        if (res.data.curMsgInfoCount > 0) {
                                            val data = res.data.curMsgInfoList.toTradeSummary().get(0)
                                            _getStockDetailSummary.postValue(data)
                                            subscribeTradeSummary(stockCode)
                                        } else {
                                            _getStockDetailSummary.postValue(TradeSummary(
                                                secCode = stockCode
                                            ))
                                        }
                                    }
                                }

                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun setListenerOrderBookTradeSum() {
        viewModelScope.launch(Dispatchers.IO) {
            setListenerTradeSumUseCase.setListener(miListener)
            setListenerOrderBookUseCase.setListenerOrderBook(miListener)
        }
    }

    private fun subscribeOrderbook(secCode: String) {
        viewModelScope.launch {
            subscribeOrderbookListUseCase.subscribeOrderBook("RG.$secCode")
        }
    }

    fun unSubscribeOrderbook(secCode: String) {
        viewModelScope.launch {
            unSubscribeOrderbookListUseCase.unSubscribeOrderBook("RG.$secCode")
        }
    }

    private fun subscribeTradeSummary(routingKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeTradeSumUseCase.subscribe("RG.$routingKey")
        }
    }

    fun unSubscribeTradeSummary(routingKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            unSubscribeTradeSumUseCase.unSubscribe("RG.$routingKey")
        }
    }

    fun setPopUpSuccessPriceAlert(isSuccess: Boolean) {
        viewModelScope.launch {
            _getPopUpSuccessPriceAlert.value = isSuccess
        }
    }

    fun clearValuePopUpPriceAlert() {
        _getPopUpSuccessPriceAlert.value = null
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
                        _getStockDetailSummary.postValue(
                            TradeSummary(
                                last = stockSummary.last,
                                close = stockSummary.close,
                                open = stockSummary.open,
                                high = stockSummary.high,
                                low = stockSummary.low,
                                iep = stockSummary.theoreticalPrice,
                                iev = stockSummary.theoreticalVolume,
                                change = stockSummary.change,
                                changePct = stockSummary.changePct,
                                tradeVolumeLot = stockSummary.tradeVolumeLot
                            )
                        )
                    }
                }
            }

            if (parsedObject.type == MIType.ORDERBOOK_SUMMARY_COMPACT) {
                Timber.tag("orderBookRealtime").d( "get realtime data")
                val orderbookSummaryProto = parsedObject.orderbookSummary
                if (orderbookSummaryProto != null) {
                    if (orderbookSummaryProto.secCode == secCode) {
                        withContext(Dispatchers.Main) {
                            _getStockOrderbookResult.postValue(orderbookSummaryProto)
                            _getOrderBookSum.postValue(
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

}