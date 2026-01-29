package com.bcasekuritas.mybest.app.feature.sectors.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.data.mapper.toTradeSummary
import com.bcasekuritas.mybest.app.domain.dto.request.StockIndexSectorRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.response.TradeSummary
import com.bcasekuritas.mybest.app.domain.interactors.GetAllStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetListStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockDetailUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockIndexSectorUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SetListenerTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeAllTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnSubscribeAllTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.PagingManager
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.datafeed.MIType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SectorDetailViewModel @Inject constructor(
    private val getStockIndexUseCase: GetStockIndexSectorUseCase,
    private val getStockDetailUseCase: GetStockDetailUseCase,
    private val setListenerTradeSumUseCase: SetListenerTradeSumUseCase,
    private val subscribeAllTradeSumUseCase: SubscribeAllTradeSumUseCase,
    private val unSubscribeAllTradeSumUseCase: UnSubscribeAllTradeSumUseCase,
    private val getListStockParamDaoUseCase: GetListStockParamDaoUseCase,
    private val getAllStockParamDaoUseCase: GetAllStockParamDaoUseCase,
    val imqConnectionListener: IMQConnectionListener
): BaseViewModel() {

    val getSubscribeStockSector = MutableLiveData<TradeSummary?>()
    val getListStockIndex = MutableLiveData<List<TradeSummary?>>()
    var getAllStockParamResult = MutableLiveData<List<StockParamObject?>>()
    var getStockCountSectorResult = MutableLiveData<Int>()
    val getAllListStock = MutableLiveData<List<TradeSummary?>>()

    private val tradeSummaryMap = mutableMapOf<String , TradeSummary>()
    private var stockAlreadySubscribe = arrayListOf<String>()
    private var sortingState = 0

    private val pagingManager = PagingManager<TradeSummary>()
    private var currentPage = 0
    private val pageSize = 20

    private var searchQuery = ""

    fun setSortOrSearch(state: Int, query: String) {
        sortingState = state
        searchQuery = query
        val data = tradeSummaryMap.values.toList()
        val listItem = if (query.isNotEmpty()) data.filter {
            it.secCode.contains(query, ignoreCase = true) ||
                    it.stockName.contains(query, ignoreCase = true)
        } else data

        val sortedList = when (state) {
            1 -> listItem.sortedByDescending { it.changePct }
            2 -> listItem.sortedBy { it.changePct }
            3 -> listItem.sortedBy { it.secCode }
            4 -> listItem.sortedByDescending { it.secCode }
            else -> listItem.sortedBy { it.secCode }
        }

        pagingManager.updateData(sortedList)
        loadPage(0)

    }

    fun getStockIndex(userId: String, sessionId: String, indexId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val stockIndexReq = StockIndexSectorRequest(userId, sessionId, indexId, 0,0, false)

            getStockIndexUseCase.invoke(stockIndexReq).collect() {resource ->
                when(resource) {
                    is Resource.Success -> {
                        if (resource.data != null) {
                            if (resource.data.dataList.size != 0) {
                                stockAlreadySubscribe.clear()
                                getStockCountSectorResult.postValue(resource.data.dataCount)
                                resource.data.dataList.forEach { item ->
                                    tradeSummaryMap[item.stockCode] = TradeSummary(secCode = item.stockCode)
                                }

                                getStockDetail(userId, sessionId, tradeSummaryMap.keys.toList())
                            }
                        }

                    }
                    else -> {}
                }
            }
        }

    }

    fun getStockDetail(userId: String, userSession: String, stockCodeList: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val stockCodeReq = StockWatchListRequest(userId,userSession, "RG", stockCodeList)

            getStockDetailUseCase.invoke(stockCodeReq).collect() { resource ->
                resource.let {res ->
                    when (res) {
                        is Resource.Success -> {
                            if (res.data != null) {
                                when (res.data.status) {
                                    0 -> {
                                        val data = res.data.curMsgInfoList.toTradeSummary()
                                        data.map { tradeSummary ->
                                            tradeSummaryMap[tradeSummary.secCode] = tradeSummary
                                        }
                                        val routingKey = tradeSummaryMap.keys.toList()
                                        getListStockParam(routingKey)
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

    fun getListStockParam(value: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            getListStockParamDaoUseCase(value).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { data ->
                            data.map {item ->
                                tradeSummaryMap[item?.stockParam?.stockCode]?.let {
                                    if (item != null) {
                                        it.stockName = item.stockParam.stockName
                                    }
                                }
                            }
                            val listData = tradeSummaryMap.values.toList()

                            pagingManager.clearData()
                            pagingManager.addData(listData)
                            loadPage(0)
                        }
                    }

                    else -> {
                    }
                }
            }
        }
    }

    fun loadPage(page: Int) {
        currentPage = if (page == 0) 0 else currentPage
        val pageData = pagingManager.getPage(page, pageSize)
        getListStockIndex.postValue(pageData)

        // handle subscribe stock code
        val routingKey = pageData.map { it.secCode }
        val newKeys = routingKey.filter { it !in stockAlreadySubscribe }

        if (newKeys.isNotEmpty()) {
            stockAlreadySubscribe.addAll(newKeys)
            subscribeStock(newKeys)
        }
    }

    fun loadNextPage() {
        currentPage++
        if (pagingManager.hasMoreData(currentPage, pageSize)) {
            loadPage(currentPage)
        }
    }

    fun setListenerTradeSummary() {
        viewModelScope.launch(Dispatchers.IO) {
            setListenerTradeSumUseCase.setListener(miListener)
        }
    }

    private fun subscribeStock(routingKey: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val latestRoutingKeyArr = routingKey.toList().map { "RG.$it" }
            subscribeAllTradeSumUseCase.subscribe(latestRoutingKeyArr)
        }
    }

    fun unSubscribeTradeSummary() {
        viewModelScope.launch(Dispatchers.IO) {
            val latestRoutingKeyArr = stockAlreadySubscribe.toList().map { "RG.$it" }
            unSubscribeAllTradeSumUseCase.unSubscribe(latestRoutingKeyArr)
        }
    }

    fun resumeSubscribe() {
        viewModelScope.launch(Dispatchers.IO) {
            if (stockAlreadySubscribe.isNotEmpty()) {
                val itemsToSubscribe = stockAlreadySubscribe.toList().map { "RG.$it" }
                subscribeAllTradeSumUseCase.subscribe(itemsToSubscribe)
            }
        }
    }

    private val miListener = MQMessageListener<MIMessage> { result ->
        viewModelScope.launch(Dispatchers.IO) {
            val parsedObject = result.protoMsg
            if (parsedObject.type == MIType.TRADE_SUMMARY) {
                Timber.tag("tradeSumRealtime").d( "get realtime data")
                val tradeSummaryProto = parsedObject.tradeSummary
                val stockCode = tradeSummaryProto.secCode

                if (stockAlreadySubscribe.contains(stockCode)) {
                    val stockName = tradeSummaryMap[stockCode]?.stockName
                    val tradeSummary = TradeSummary(
                        secCode = tradeSummaryProto.secCode,
                        change = tradeSummaryProto.change,
                        changePct = tradeSummaryProto.changePct,
                        last = tradeSummaryProto.last,
                        close = tradeSummaryProto.close,
                        stockName = stockName!!
                    )
                    tradeSummaryMap[stockCode] = tradeSummary
                    val updateList = tradeSummaryMap.values.toList()

                    // check if user is doing seaching stock
                    val listItem = if (searchQuery.isNotEmpty()) updateList.filter {
                        it.secCode.contains(searchQuery, ignoreCase = true) ||
                                it.stockName.contains(searchQuery, ignoreCase = true)
                    } else updateList

                    val sortedList = when (sortingState) {
                        1 -> listItem.sortedByDescending { it.changePct }
                        2 -> listItem.sortedBy { it.changePct }
                        3 -> listItem.sortedBy { it.secCode }
                        4 -> listItem.sortedByDescending { it.secCode }
                        else -> listItem.sortedBy { it.secCode }
                    }

                    pagingManager.updateData(sortedList)

                    withContext(Dispatchers.Main) {
                        getSubscribeStockSector.postValue(tradeSummaryMap[stockCode])
                    }
                }
            }
        }
    }

}