package com.bcasekuritas.mybest.app.feature.stockdetail.runningtrade

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.LatestTradeDetailRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockTradeReq
import com.bcasekuritas.mybest.app.domain.dto.response.TradeDetail
import com.bcasekuritas.mybest.app.domain.interactors.GetLatestTradeDetailUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SetListenerRunningTradeUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeRunningTradeUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnSubscribeRunningTradeUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockTradeUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.message.MQMessageEvent
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.datafeed.MIType
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeDetailData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StockDetailRunningTradeViewModel @Inject constructor(
    private val setListenerRunningTradeUseCase: SetListenerRunningTradeUseCase,
    private val subscribeRunningTradeUseCase: SubscribeRunningTradeUseCase,
    private val unSubscribeRunningTradeUseCase: UnSubscribeRunningTradeUseCase,
    private val getStockTradeUseCase: GetStockTradeUseCase,
    private val getLatestTradeDetailUseCase: GetLatestTradeDetailUseCase
) : BaseViewModel() {

    private var MAX_RUNNING_TRADE_CAPACITY = 0
    private var currTradeIndex = 0
    private var rowId: Long = 0
    private val runningTrades: ArrayList<TradeDetail> = ArrayList<TradeDetail>()

    val getRunningTradeData = MutableLiveData<List<TradeDetail>>()
    val getLiveRunningTradeData = MutableLiveData<List<TradeDetail>>()
    var secCode = ""

    fun setMaxRunningTrade(itemCount: Int) {
        MAX_RUNNING_TRADE_CAPACITY = itemCount
    }

    fun getRunningTrade(userId: String, sessionId: String, stockCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = StockTradeReq(userId, sessionId, stockCode, 0, 0.0, 20, 0L)
            secCode = stockCode

            getStockTradeUseCase.invoke(request).collect() { resources ->
                resources.let {res->
                    when (res) {
                        is Resource.Success -> {
                            if (res.data != null) {
                                var latestTradeData = res.data.tradeDetailDataList

                                if (latestTradeData.isNotEmpty()) {
                                    var lastItems: List<TradeDetailData>

                                    if (latestTradeData.size > 15){
                                        lastItems = latestTradeData.takeLast(15)
                                    }else{
                                        lastItems = latestTradeData
                                    }

                                    lastItems.map {
                                        processTrade(it, stockCode)
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

    fun getLatestUniqueId(): Long {
        return rowId - 1
    }

    private fun processTrade(tradeDetailData: TradeDetailData, stockCode: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val tradeListSize: Int = runningTrades.size
                val price = tradeDetailData.price.toInt()
                val change = tradeDetailData.change
                val volume = tradeDetailData.volume.toInt()

                if (tradeListSize > 0 && currTradeIndex < tradeListSize) {
                    runningTrades[currTradeIndex] = TradeDetail(
                        currTradeIndex.toLong(),
                        rowId,
                        tradeDetailData.boardCode,
                        tradeDetailData.secCode,
                        tradeDetailData.tradeTime,
                        price,
                        tradeDetailData.closePrice,
                        change,
                        volume,
                        tradeDetailData.buyerCode,
                        tradeDetailData.sellerCode,
                        tradeDetailData.buyerType,
                        tradeDetailData.sellerType
                    )
                } else {
                    runningTrades.add(
                        TradeDetail(
                            currTradeIndex.toLong(),
                            rowId,
                            tradeDetailData.boardCode,
                            tradeDetailData.secCode,
                            tradeDetailData.tradeTime,
                            price,
                            tradeDetailData.closePrice,
                            change,
                            volume,
                            tradeDetailData.buyerCode,
                            tradeDetailData.sellerCode,
                            tradeDetailData.buyerType,
                            tradeDetailData.sellerType
                        )
                    )
                }
                currTradeIndex = ++currTradeIndex % MAX_RUNNING_TRADE_CAPACITY
                rowId++

                withContext(Dispatchers.Main) {
                    getRunningTradeData.postValue(runningTrades)
                }
            }
        } catch (e:Exception){
            Timber.tag("StockDetailRunningTrade").d("Potentially out of bound")
        }
    }

    fun setListenerRunningTrade() {
        viewModelScope.launch(Dispatchers.IO) {
            setListenerRunningTradeUseCase.setListener(miListener)
        }
    }

    fun subscribeRunningTrade(stockCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeRunningTradeUseCase.subscribe(stockCode)
        }
    }

    fun unSubscribeRunningTrade(stockCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            unSubscribeRunningTradeUseCase.unSubscribe(stockCode)
        }
    }

    private val miListener = MQMessageListener<MIMessage> { event ->
        viewModelScope.launch(Dispatchers.IO) {
            val parsedObject = event.protoMsg
            if (parsedObject.type == MIType.TRADE_DETAIL_DATA) {
                val tradeDetailData = parsedObject.tradeDetailData
                if (secCode == tradeDetailData.secCode){
                    processTrade(tradeDetailData, tradeDetailData.secCode)
                }
            }
        }
    }
}