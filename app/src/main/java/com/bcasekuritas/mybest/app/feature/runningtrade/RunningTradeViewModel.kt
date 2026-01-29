package com.bcasekuritas.mybest.app.feature.runningtrade

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.layout.UIDialogRunningTradeModel
import com.bcasekuritas.mybest.app.domain.dto.request.LatestTradeDetailRequest
import com.bcasekuritas.mybest.app.domain.dto.request.MarketSessionReq
import com.bcasekuritas.mybest.app.domain.dto.response.TradeDetail
import com.bcasekuritas.mybest.app.domain.interactors.GetFilterRunningTradeUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetLatestTradeDetailUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetMarketSessionUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SetListenerRunningTradeUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeRunningTradeUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnSubscribeRunningTradeUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.event.SingleLiveEvent
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.bcas.MarketSessionInfo
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.datafeed.MIType
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeDetailData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RunningTradeViewModel @Inject constructor(
    private val setListenerRunningTradeUseCase: SetListenerRunningTradeUseCase,
    private val subscribeRunningTradeUseCase: SubscribeRunningTradeUseCase,
    private val unSubscribeRunningTradeUseCase: UnSubscribeRunningTradeUseCase,
    private val getLatestTradeDetailUseCase: GetLatestTradeDetailUseCase,
    private val getMarketSessionUseCase: GetMarketSessionUseCase,
    private val getFilterRunningTradeUseCase: GetFilterRunningTradeUseCase,
    val imqConnectionListener: IMQConnectionListener
): BaseViewModel() {

    private var MAX_RUNNING_TRADE_CAPACITY = 10
    private var currTradeIndex = 0
    private var rowId: Long = 0
    private val runningTrades: ArrayList<TradeDetail> = arrayListOf()
    var isRunningTradeStart = false

    private var filterStock = arrayListOf<String>()
    private var minPrice: Double? = null
    private var maxPrice: Double? = null
    private var minVolume: Double? = null
    private var maxVolume: Double? = null
    private var minChange: Double? = null
    private var maxChange: Double? = null

    val getRunningTradeData = MutableLiveData<List<TradeDetail>>()
    val getMarketSessionResult = MutableLiveData<MarketSessionInfo>()
    val getDefaultFilter = SingleLiveEvent<UIDialogRunningTradeModel>()

    fun setFilter(filter: UIDialogRunningTradeModel) {
        minPrice = if (filter.priceFrom == 0.0) null else filter.priceFrom
        maxPrice = if (filter.priceTo == 0.0) null else filter.priceTo
        minChange = if (filter.changeFrom == 0.0) null else filter.changeFrom
        maxChange = if (filter.changeTo == 0.0) null else filter.changeTo
        minVolume = if (filter.volumeFrom == 0.0) null else filter.volumeFrom
        maxVolume = if (filter.volumeTo == 0.0) null else filter.volumeTo
    }

    fun setFilterStock(stocks: List<String>) {
        filterStock.clear()
        if (stocks.isNotEmpty()) {
            filterStock.addAll(stocks)
        }
        Log.d("filterr", "total filter stock ${filterStock.size}")

    }

    private fun filterData(
        data: List<TradeDetailData>,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minVolume: Double? = null,
        maxVolume: Double? = null,
        minChange: Double? = null,
        maxChange: Double? = null
    ):List<TradeDetailData>  {
        val filterStockCode = if (filterStock.isEmpty()) data else data.filter {item -> filterStock.contains(item.secCode) }
        return filterStockCode.filter { item ->
            (minPrice == null || item.price >= minPrice) &&
            (maxPrice == null || item.price <= maxPrice) &&
            (minChange == null || item.change.div(item.closePrice).times(100) >= minChange) &&
            (maxChange == null || item.change.div(item.closePrice).times(100) <= maxChange) &&
            (minVolume == null || item.volume >= minVolume) &&
            (maxVolume == null || item.volume <= maxVolume)
        }
    }

    fun setMaxRunningTrade(itemCount: Int) {
        MAX_RUNNING_TRADE_CAPACITY = itemCount
    }

    fun startRunningTrade(userId: String, sessionId: String, isMarketBreak: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val latestTradeDetailReq = LatestTradeDetailRequest(userId, sessionId)

            getLatestTradeDetailUseCase.invoke(latestTradeDetailReq).collect() {resources ->
                resources.let {
                    when (resources) {
                        is Resource.Success -> {
                            try {
                                if (resources.data != null) {
                                    if (!isMarketBreak) {
                                        val filterData = filterData(resources.data, minPrice, maxPrice, minVolume, maxVolume, minChange, maxChange)
                                        if (filterData.isNotEmpty()) {
                                            filterData.forEach {item ->
                                                processTrade(item)
                                            }
                                        }
                                    } else {
                                        val listItem = resources.data.takeLast(MAX_RUNNING_TRADE_CAPACITY).reversed()
                                        var index = 0
                                        val filterList = filterData(listItem, minPrice, maxPrice, minVolume, maxVolume, minChange, maxChange)
                                        if (filterList.isNotEmpty()) {
                                            val data = filterList.map { item ->
                                                val volume = if (item.boardCode.equals("NG")) item.volume else item.volume.div(100)
                                                val stockCode = if (!item.boardCode.equals("RG")) item.secCode + ".${item.boardCode}" else item.secCode

                                                index++
                                                TradeDetail(
                                                    index.toLong(),
                                                    index.toLong(),
                                                    item.boardCode,
                                                    stockCode,
                                                    item.tradeTime,
                                                    item.price.toInt(),
                                                    item.closePrice,
                                                    item.change,
                                                    volume.toInt(),
                                                    item.buyerCode,
                                                    item.sellerCode,
                                                    item.buyerType,
                                                    item.sellerType
                                                )
                                            }
                                            getRunningTradeData.postValue(data)

                                        }
                                    }
                                }
                            }catch (e: Exception){
                                Timber.tag("RunningTradeView").d("Potentially Out Of Bound")
                            }
                        } else -> {}
                    }
                }
            }
        }
    }

    fun getLatestUniqueId(): Long {
        return rowId - 1
    }

    private fun processTrade(tradeDetailData: TradeDetailData) {
        viewModelScope.launch(Dispatchers.IO) {
            val tradeListSize: Int = runningTrades.size
            val price = tradeDetailData.price.toInt()
            val change = tradeDetailData.change
            var volume = tradeDetailData.volume.toInt()

            var stockCode = tradeDetailData.secCode
            if (!tradeDetailData.boardCode.equals("RG")) {
                stockCode = tradeDetailData.secCode + ".${tradeDetailData.boardCode}"
            }

            if (!tradeDetailData.boardCode.equals("NG")) {
                volume = volume.div(100)
            }

            if (tradeListSize > 0 && currTradeIndex < tradeListSize) {
                runningTrades[currTradeIndex] = TradeDetail(
                    currTradeIndex.toLong(),
                    rowId,
                    tradeDetailData.boardCode,
                    stockCode,
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
                        stockCode,
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
    }

    fun setListenerRunningTrade() {
        viewModelScope.launch(Dispatchers.IO) {
            setListenerRunningTradeUseCase.setListener(miListener)
        }
    }

    fun subscribeRunningTrade() {
        if (!isRunningTradeStart) {
            isRunningTradeStart = true
            viewModelScope.launch(Dispatchers.IO) {
                subscribeRunningTradeUseCase.subscribe("#")
            }
        }
    }

    fun unSubscribeRunningTrade() {
        isRunningTradeStart = false
        viewModelScope.launch(Dispatchers.IO) {
            unSubscribeRunningTradeUseCase.unSubscribe("#")
        }
    }

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

    fun getDefaultFilterRunningTrade(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getFilterRunningTradeUseCase.invoke(userId).collect() { resource ->
                when (resource) {
                    is Resource.Success -> {
                        if (resource.data != null) {
                            val filter = UIDialogRunningTradeModel(
                                resource.data.category,
                                resource.data.indexSectorId,
                                resource.data.minPrice,
                                resource.data.maxPrice,
                                resource.data.minChange,
                                resource.data.maxChange,
                                resource.data.minVolume,
                                resource.data.maxVolume,
                                resource.data.stockCodes
                            )
                            getDefaultFilter.postValue(filter)
                        } else {
                            getDefaultFilter.postValue(UIDialogRunningTradeModel())
                        }
                    }
                    else -> {}
                }

            }
        }
    }

    private val miListener = MQMessageListener<MIMessage> { event ->
        viewModelScope.launch(Dispatchers.IO) {
            val parsedObject = event.protoMsg
            if (parsedObject.type == MIType.TRADE_DETAIL_DATA) {
                Timber.tag("runningTradeRealtime").d( "get realtime data")
                val tradeDetailData = parsedObject.tradeDetailData
                val filterData = filterData(listOf(tradeDetailData), minPrice, maxPrice, minVolume, maxVolume, minChange, maxChange)
                if (filterData.isNotEmpty()) {
                    processTrade(filterData[0])
                }
            }
        }
    }

}