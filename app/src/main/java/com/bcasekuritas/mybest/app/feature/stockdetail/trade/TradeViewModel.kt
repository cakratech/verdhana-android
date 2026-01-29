package com.bcasekuritas.mybest.app.feature.stockdetail.trade

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.TradeBookRequest
import com.bcasekuritas.mybest.app.domain.interactors.GetStockTradeUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetTradeBookTimeUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.datafeed.MIType
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeBookTimeResponse
import com.bcasekuritas.mybest.app.domain.dto.response.TradeDetailData
import com.bcasekuritas.mybest.app.domain.interactors.GetTradeBookUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SetListenerRunningTradeUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeRunningTradeUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnSubscribeRunningTradeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TradeViewModel @Inject constructor(
    private val getStockTradeUseCase: GetStockTradeUseCase,
    private val getTradeBookTimeUseCase: GetTradeBookTimeUseCase,
    private val setListenerRunningTradeUseCase: SetListenerRunningTradeUseCase,
    private val subscribeRunningTradeUseCase: SubscribeRunningTradeUseCase,
    private val unSubscribeRunningTradeUseCase: UnSubscribeRunningTradeUseCase,
    private val getTradeBookUseCase: GetTradeBookUseCase
) : BaseViewModel() {

    val getTradeBookTimeResult = MutableLiveData<Resource<TradeBookTimeResponse?>>()
    val getStockTradPriceeResult = MutableLiveData<List<TradeDetailData>>()
    private val tradePriceMap = mutableMapOf<Double, TradeDetailData>()
    var stockCode = ""

    fun getStockTrade(userId: String, sessionId: String, secCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            stockCode = secCode
            val stockTradeReq = TradeBookRequest(userId, sessionId, secCode)
            getTradeBookUseCase.invoke(stockTradeReq).collect() { resource ->
                resource.let { res ->
                    when (res) {
                        is Resource.Success -> {
                            try {
                                if (res.data != null) {
                                    tradePriceMap.clear() // Ensure it's fully cleared before adding new data

                                    res.data.tradeBookList.forEach { item ->
                                        item.price?.let {
                                            tradePriceMap[item.price] = TradeDetailData(
                                                item.secCode,
                                                item.price,
                                                item.totalFreq,
                                                item.buyFreq,
                                                item.sellFreq,
                                                item.totalLot,
                                                item.buyLot,
                                                item.sellLot
                                            )
                                        }
                                    }

                                    try {

                                        // Create a fresh copy before sorting
                                        val sortedList = tradePriceMap.values.sortedBy { it.price }
                                        getStockTradPriceeResult.postValue(sortedList)
                                    } catch (e: Exception){
                                        Timber.tag("TradeViewModel").d("Possibly Null")
                                    }

                                    subscribeStockTrade(secCode)
                                }
                            }catch (e: Exception){
                                Timber.tag("TradeViewModel").d(e)
                                Timber.tag("TradeViewModel").d("Possibly Out Of Bound")
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun getTradeBookTime(userId: String, sessionId: String, secCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val stockTradeReq = TradeBookRequest(userId, sessionId, secCode)
            getTradeBookTimeUseCase.invoke(stockTradeReq).collect() { resource ->
                resource.let {
                    getTradeBookTimeResult.postValue(it)
                }
            }
        }
    }

    fun setListenerStockTrade() {
        viewModelScope.launch(Dispatchers.IO) {
            setListenerRunningTradeUseCase.setListener(miListener)
        }
    }

    private fun subscribeStockTrade(secCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeRunningTradeUseCase.subscribe("RG.$secCode")
        }
    }

    fun unSubscribeStockTrade(secCode: String) {
        viewModelScope.launch {
            unSubscribeRunningTradeUseCase.unSubscribe("RG.$secCode")
        }
    }

    private val miListener = MQMessageListener<MIMessage> { event ->
        viewModelScope.launch(Dispatchers.IO) {
            val parsedObject = event.protoMsg
            if (parsedObject.type == MIType.TRADE_DETAIL_DATA) {
                val tradeDetailData = parsedObject.tradeDetailData
                if (stockCode == tradeDetailData.secCode) {
                    tradePriceMap[tradeDetailData.price] = TradeDetailData(
                        tradeDetailData.secCode,
                        tradeDetailData.price,
                        tradeDetailData.totalFreq,
                        tradeDetailData.buyFreq,
                        tradeDetailData.sellFreq,
                        tradeDetailData.totalLot,
                        tradeDetailData.buyLot,
                        tradeDetailData.sellLot
                    )
                    try {
                        val sortedList = tradePriceMap.values.sortedBy { it.price }
                        withContext(Dispatchers.Main) {
                            getStockTradPriceeResult.postValue(sortedList)
                        }
                    } catch (e: Exception){
                        Timber.tag("TradeViewModel").d("Possibly Null")
                    }
                }
            }
        }
    }
}

