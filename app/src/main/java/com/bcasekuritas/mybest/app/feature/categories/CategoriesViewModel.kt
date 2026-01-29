package com.bcasekuritas.mybest.app.feature.categories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.StockRankInfoRequest
import com.bcasekuritas.mybest.app.domain.dto.response.CategoriesItem
import com.bcasekuritas.mybest.app.domain.interactors.GetListStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockRankInfoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SetListenerTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeAllTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnSubscribeAllTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.PagingManager
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.datafeed.MIType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val getStockRankInfoUseCase: GetStockRankInfoUseCase,
    private val setListenerTradeSumUseCase: SetListenerTradeSumUseCase,
    private val subscribeAllTradeSumUseCase: SubscribeAllTradeSumUseCase,
    private val unSubscribeAllTradeSumUseCase: UnSubscribeAllTradeSumUseCase,
    private val getListStockParamDaoUseCase: GetListStockParamDaoUseCase,
    val imqConnectionListener: IMQConnectionListener
): BaseViewModel() {

    val getCategoriesDataResult = MutableLiveData<List<CategoriesItem>>()
    val getCategoriesDataSubscribeResult = MutableLiveData<CategoriesItem>()
    private val categoriesMap = mutableMapOf<String, CategoriesItem>()
    private var stockAlreadySubscribe = arrayListOf<String>()

    private val pagingManager = PagingManager<CategoriesItem>()
    private var currentPage = 0
    private val pageSize = 20

    private fun loadPage(page: Int) {
        currentPage = if (page == 0) 0 else currentPage
        val pageData = pagingManager.getPage(page, pageSize)
        Log.d("categoriess", "page $currentPage list data: ${pageData.size}")
        getCategoriesDataResult.postValue(pageData)

        // handle subscribe stock code
        val routingKey = pageData.map { it.stockCode }
        val newKeys = routingKey.filter { it !in stockAlreadySubscribe }

        if (newKeys.isNotEmpty()) {
            stockAlreadySubscribe.addAll(newKeys)
            subscribeTradeSummary(newKeys)
        }
    }

    fun loadNextPage() {
        currentPage++
        if (pagingManager.hasMoreData(currentPage, pageSize)) {
            loadPage(currentPage)
        }
    }

    fun getCategoriesData(userId: String, sessionId: String, sortAscending: Int, sortType: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            categoriesMap.clear()
            stockAlreadySubscribe.clear()
            val categoriesRequest = StockRankInfoRequest(userId,sessionId, sortAscending, sortType, 0, 0)

            getStockRankInfoUseCase.invoke(categoriesRequest).collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        Log.d("categoriess", "-------- list data: ${resource.data?.rankInfoCount} --------")
                        resource.data?.rankInfoList?.forEach {
                            val price = if (it.priceInfo.lastPrice != 0.0f) it.priceInfo.lastPrice else it.priceInfo.prevPrice
                            categoriesMap[it.stockCode] = CategoriesItem(
                                stockCode = it.stockCode,
                                changePct = it.priceInfo.chgPercent.toDouble(),
                                change = it.priceInfo.change.toDouble(),
                                lastPrice = price.toDouble(),
                                stockName = it.stockName
                            )
                        }

                        val stockCodes = categoriesMap.keys.toList()
                        getListStockParam(stockCodes)
                    }
                    else -> {}
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
                                categoriesMap[item?.stockParam?.stockCode]?.let {
                                    if (item != null) {
                                        it.stockName = item.stockParam.stockName
                                    }
                                }
                            }

                            val listData: List<CategoriesItem>
                            synchronized(categoriesMap) {
                                listData = ArrayList(categoriesMap.values)
                            }

                            pagingManager.clearData()
                            pagingManager.addData(listData)
                            loadPage(0)
                        }
                    }

                    else -> {
                        // Handle other cases if needed
                    }
                }
            }
        }
    }

    private fun subscribeTradeSummary(routingKey: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val routingKeys = routingKey.map { "RG.$it" }
            delay(500)
            subscribeAllTradeSumUseCase.subscribe(routingKeys)
            Log.d("categoriess", "stock already subscribe: ${stockAlreadySubscribe.size}")
        }
    }

    fun resumeSubscribe() {
        viewModelScope.launch(Dispatchers.IO) {
            if (stockAlreadySubscribe.isNotEmpty()) {
                val routingKeys = stockAlreadySubscribe.toList().map { "RG.$it" }
                subscribeAllTradeSumUseCase.subscribe(routingKeys)
            }
        }
    }

    fun setListenerTradeSummary() {
        viewModelScope.launch(Dispatchers.IO) {
            setListenerTradeSumUseCase.setListener(miListener)
        }
    }

    fun unSubscribeTradeSummary(isDestroy: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val routingKeys = stockAlreadySubscribe.toList().map { "RG.$it" }
            unSubscribeAllTradeSumUseCase.unSubscribe(routingKeys)
            if (isDestroy) {
                stockAlreadySubscribe.clear()
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
                    val stockName = categoriesMap[stockCode]?.stockName
                    val price = if (tradeSummaryProto.last != 0.0) tradeSummaryProto.last else tradeSummaryProto.close
                    val categoriesItem = CategoriesItem(
                        stockCode = tradeSummaryProto.secCode,
                        changePct = tradeSummaryProto.changePct,
                        change = tradeSummaryProto.change,
                        lastPrice = price,
                        stockName = stockName!!
                    )
                    categoriesMap[stockCode] = categoriesItem
                    withContext(Dispatchers.Main) {
                        getCategoriesDataSubscribeResult.postValue(categoriesMap[stockCode])
                    }
                }
            }
        }
    }

}