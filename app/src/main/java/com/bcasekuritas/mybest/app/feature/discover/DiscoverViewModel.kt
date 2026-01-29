package com.bcasekuritas.mybest.app.feature.discover

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.mapper.toIpoListData
import com.bcasekuritas.mybest.app.domain.dto.request.ChartIntradayRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IPOListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IndexSectorDataRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IndexSectorRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IndiceDataRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockRankInfoRequest
import com.bcasekuritas.mybest.app.domain.dto.response.CategoriesItem
import com.bcasekuritas.mybest.app.domain.dto.response.IndexSectorDetailData
import com.bcasekuritas.mybest.app.domain.dto.response.IndiceData
import com.bcasekuritas.mybest.app.domain.dto.response.IpoData
import com.bcasekuritas.mybest.app.domain.interactors.GetChartIntradayPriceUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetIPOListUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetIndexSectorDetailDataUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetIndexSectorUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetIndiceDataUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockRankInfoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SetListenerIndiceDataUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SetListenerTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeAllIndiceDataUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeAllTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeIndiceDataUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnSubscribeAllIndiceDataUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnSubscribeAllTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnSubscribeIndiceDataUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.converter.GET_IMAGE_SECTOR
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.chart.Cf.CFMessage.IntradayPriceInfo
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.datafeed.MIType
import com.bcasekuritas.rabbitmq.proto.news.ViewIndexSector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val getIndiceDataUseCase: GetIndiceDataUseCase,
    private val setListenerIndiceDataUseCase: SetListenerIndiceDataUseCase,
    private val subscribeIndiceDataUseCase: SubscribeIndiceDataUseCase,
    private val unSubscribeIndiceDataUseCase: UnSubscribeIndiceDataUseCase,
    private val subscribeAllIndiceDataUseCase: SubscribeAllIndiceDataUseCase,
    private val unSubscribeAllIndiceDataUseCase: UnSubscribeAllIndiceDataUseCase,
    private val getIndexSectorUseCase: GetIndexSectorUseCase,
    private val getIndexSectorDetailDataUseCase: GetIndexSectorDetailDataUseCase,
    private val getStockRankInfoUseCase: GetStockRankInfoUseCase,
    private val setListenerTradeSumUseCase: SetListenerTradeSumUseCase,
    private val subscribeAllTradeSumUseCase: SubscribeAllTradeSumUseCase,
    private val unSubscribeAllTradeSumUseCase: UnSubscribeAllTradeSumUseCase,
    private val chartIntradayPriceUseCase: GetChartIntradayPriceUseCase,
    private val getIPOListUseCase: GetIPOListUseCase,
    val imqConnectionListener: IMQConnectionListener
    ): BaseViewModel() {

    val getIndiceDataResult = MutableLiveData<IndiceData>()
    val getIndexDetailDataResult = MutableLiveData<List<IndexSectorDetailData?>?>()
    val getSectorDetailDataResult = MutableLiveData<List<IndexSectorDetailData?>?>()
    val getCategoriesDataResult = MutableLiveData<List<CategoriesItem>>()
    val showSessionExpired = MutableLiveData<Boolean>()
    val getListIndexForSummary = MutableLiveData<List<ViewIndexSector>>()
    val getSummaryIsNull = MutableLiveData<Boolean>()
    val getChartIntradayResult = MutableLiveData<List<IntradayPriceInfo>>()
    val getIpoListResult = MutableLiveData<List<IpoData>>()

    private var indexCode = ""
    fun setIndiceSummary(code: String) {
        indexCode = code
    }

    private val indexMap = mutableMapOf<String , IndexSectorDetailData>()
    private val sectorMap = mutableMapOf<String , IndexSectorDetailData>()
    private val categoriesMap = mutableMapOf<String, CategoriesItem>()
    private val latestRoutingKey = mutableListOf<String>()
    private var listIndexAlreadySubs = listOf<String>()

    fun getChartIntraday(userId: String, sessionId: String, itemCode: String, ssDateFrom: Long, ssDateTo: Long,  timeUnit: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val chartRequest = ChartIntradayRequest(userId, sessionId, itemCode, "", timeUnit, ssDateFrom, ssDateTo)

            chartIntradayPriceUseCase.invoke(chartRequest).collect() {resource->
                when (resource) {
                    is Resource.Success -> {
                        getChartIntradayResult.postValue(resource.data?.priceInfoList)
                    }
                    else -> {}
                }
            }
        }
    }

    fun getCategoriesData(userId: String, sessionId: String, sortAscending: Int, sortType: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            categoriesMap.clear()
            val categoriesRequest = StockRankInfoRequest(userId,sessionId, sortAscending, sortType, 0, 10)

            getStockRankInfoUseCase.invoke(categoriesRequest).collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        when (resource.data?.status) {
                            0 -> {
                                resource.data.rankInfoList?.map {
                                    categoriesMap[it.stockCode] = CategoriesItem(
                                        stockCode = it.stockCode,
                                        changePct = it.priceInfo.chgPercent.toDouble()
                                    )
                                }
                                getCategoriesDataResult.postValue(categoriesMap.values.toList())
                                val routingKey = categoriesMap.keys.toList()

                                if (latestRoutingKey.size != 0) {
                                    unSubscribeTradeSummary()
                                    latestRoutingKey.clear()
                                }

                                if (routingKey.isNotEmpty()) {
                                    latestRoutingKey.addAll(routingKey)
                                    subscribeTradeSummary()
                                }
                            }
                            2 -> showSessionExpired.postValue(true)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun subscribeTradeSummary() {
        viewModelScope.launch(Dispatchers.IO) {
            val latestRoutingKeyArr = latestRoutingKey.toList().map { "RG.$it" }
            subscribeAllTradeSumUseCase.subscribe(latestRoutingKeyArr)
        }
    }

    fun unSubscribeTradeSummary() {
        viewModelScope.launch(Dispatchers.IO) {
            val latestRoutingKeyArr = latestRoutingKey.toList().map { "RG.$it" }
            unSubscribeAllTradeSumUseCase.unSubscribe(latestRoutingKeyArr)
        }
    }

    fun getIndiceData(userId: String, sessionId: String, code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val indiceRequest = IndiceDataRequest(userId, sessionId, code)
            getIndiceDataUseCase.invoke(indiceRequest).collect() {resource ->
                resource.let { response ->
                    when (response) {
                        is Resource.Success -> {
                            if (response.data?.status == 2) {
                                showSessionExpired.postValue(true)
                            }
                            response.data?.curMsgInfoList?.let {
                                if (it.isNotEmpty()) {
                                    val data = it[0].inSummary
                                    if (data != null) {
                                        getSummaryIsNull.postValue(false)
                                        getIndiceDataResult.postValue(
                                            IndiceData(
                                                data.indiceCode,
                                                data.indiceVal,
                                                data.close,
                                                data.open,
                                                data.high,
                                                data.low,
                                                data.change,
                                                data.chgPercent,
                                                data.mktTradeInfo.marketVal,
                                                data.mktTradeInfo.marketVol,
                                                data.mktTradeInfo.marketFreq,
                                                data.mktTradeInfo.totalUp,
                                                data.mktTradeInfo.totalNoChange,
                                                data.mktTradeInfo.totalDown,
                                                data.mktTradeInfo.totalUnTrade
                                            )
                                        )
                                        subscribeIndiceDataSummary(code)
                                    }
                                } else {
                                    getSummaryIsNull.postValue(true)
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun getIndexSectorData(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = IndexSectorRequest(userId, sessionId, 0)

            getIndexSectorUseCase.invoke(request).collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        val indexData = resource.data?.filter { !it!!.sector }?.sortedBy { it?.indexCode }?.take(10)
                        val sectorData = resource.data?.filter { it!!.sector }?.sortedBy { it?.indexCode }?.take(10)

                        sectorData?.map {
                            if (it != null) {
                                sectorMap[it.indexCode] = IndexSectorDetailData(id = it.id, indiceCode = it.indexCode, stockCount = it.stockCount, indexName = it.indexName, idImg = it.indexCode.GET_IMAGE_SECTOR())
                            }
                        }

                        indexData?.map {
                            if (it != null) {
                                indexMap[it.indexCode] = IndexSectorDetailData(id = it.id, indiceCode = it.indexCode, indexName = it.indexName)
                            }
                        }

                        val listAllIndex = resource.data?.filter { !it!!.sector }?.sortedBy { it?.indexCode }
                        getListIndexForSummary.postValue(listAllIndex?.filterNotNull())

                        // merge two list
                        val combinedListData = indexData?.toMutableList()
                        if (sectorData != null) { combinedListData?.addAll(sectorData) }
                        val listItemCode = arrayListOf<String>()
                        combinedListData?.map { if (it != null) { listItemCode.add(it.indexCode) } }

                        if (listItemCode.size != 0) {
                            getIndexSectorDetailData(userId, sessionId, listItemCode)
                        }

                    }
                    else -> {}
                }

            }


        }
    }

    private fun getIndexSectorDetailData(userId: String, sessionId: String, listItem: List<String>) {
        // type 1: Index, type 2: Sector
        viewModelScope.launch(Dispatchers.IO) {
            val request = IndexSectorDataRequest(userId, sessionId, "RG", listItem)

            getIndexSectorDetailDataUseCase.invoke(request).collect() {
                when(it) {
                    is Resource.Success -> {
                        it.data?.map {
                            if (indexMap.containsKey(it?.indiceCode)) {
                                if (it != null) {
                                    val id = indexMap[it.indiceCode]?.id
                                    val indexName = indexMap[it.indiceCode]?.indexName
                                    indexMap[it.indiceCode] = IndexSectorDetailData(
                                        id!!,
                                        it.indiceCode,
                                        it.indiceVal,
                                        it.change,
                                        it.chgPercent,
                                        indexName = indexName?:""
                                    )
                                }
                            }
                            else if (sectorMap.containsKey(it?.indiceCode)) {
                                if (it != null) {
                                    val id = sectorMap[it.indiceCode]?.id
                                    val stockCount = sectorMap[it.indiceCode]?.stockCount
                                    val sectorName = sectorMap[it.indiceCode]?.indexName
                                    sectorMap[it.indiceCode] = IndexSectorDetailData(
                                        id!!,
                                        it.indiceCode,
                                        it.indiceVal,
                                        it.change,
                                        it.chgPercent,
                                        stockCount!!,
                                        it.indiceCode.GET_IMAGE_SECTOR(),
                                        sectorName?:""
                                    )
                                }
                            }
                        }

                        if (indexMap.size != 0 && sectorMap.size != 0) {
                            val listIndex = indexMap.keys.toList()
                            val combineList = listIndex.plus(sectorMap.keys)
                            subscribeIndexSector(combineList)
                            listIndexAlreadySubs = listIndex
                            val sortedIndex = indexMap.values.sortedBy { it.indiceCode }
                            getIndexDetailDataResult.postValue(sortedIndex)

                            val sortedSector = sectorMap.values.sortedBy { item -> item.indiceCode }
                            getSectorDetailDataResult.postValue(sortedSector)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun getIpoList(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = IPOListRequest(userId, sessionId, false, 2, 10, 0)

            getIPOListUseCase.invoke(request).collect() {resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            val response = it.data?.pipelinesIpoListDataList?.toIpoListData()

                            getIpoListResult.postValue(response?.filterNotNull())
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun setListenerIndiceAndTradeSum() {
        viewModelScope.launch(Dispatchers.IO) {
            setListenerTradeSumUseCase.setListener(miListener)
            setListenerIndiceDataUseCase.setListenerIndiceData(miListener)
        }
    }

    private fun subscribeIndexSector(listItem: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeAllIndiceDataUseCase.subscribe(listItem)
        }
    }

    private fun subscribeIndiceDataSummary(routingKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val isAlreadySubs = listIndexAlreadySubs.contains(routingKey)
            if (!isAlreadySubs) {
                subscribeIndiceDataUseCase.subscribe(routingKey)
            }
        }
    }

    fun unSubscribeIndiceSummary(routingKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val indexListBottom = listIndexAlreadySubs.contains(routingKey)
            if (!indexListBottom) {
                unSubscribeIndiceDataUseCase.unSubscribe(routingKey)
            }
        }
    }

    fun unSubscribeAllIndiceSummary(routingKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
//            val listIndexAlreadySubsArr = listIndexAlreadySubs.toList() // Copy before iteration
//            unSubscribeAllIndiceDataUseCase.unSubscribe(listIndexAlreadySubsArr)
            val indexMapKeysArr = indexMap.keys.toList() // Copy before iteration
            unSubscribeAllIndiceDataUseCase.unSubscribe(indexMapKeysArr)
            val sectorMapKeysArr = sectorMap.keys.toList() // Copy before iteration
            unSubscribeAllIndiceDataUseCase.unSubscribe(sectorMapKeysArr)
            unSubscribeIndiceDataUseCase.unSubscribe(routingKey)
        }
    }

    private val miListener = MQMessageListener<MIMessage> { result ->
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val parsedObject = result.protoMsg
                if (parsedObject.type == MIType.TRADE_SUMMARY) {
                    Timber.tag("tradeSumRealtime").d("get realtime data")
                    val data = parsedObject.tradeSummary
                    val stockCode = data.secCode

                    // for categories item
                    if (categoriesMap.containsKey(stockCode)) {
                        val categoriesItem = CategoriesItem(
                            stockCode = data.secCode,
                            changePct = data.changePct,
                        )
                        categoriesMap[stockCode] = categoriesItem
                        withContext(Dispatchers.Main) {
                            getCategoriesDataResult.postValue(categoriesMap.values.toList())
                        }
                    }
                }

                if (parsedObject.type == MIType.INDICE_SUMMARY) {
                    Timber.tag("indiceSumRealtime").d( "get realtime data")
                    val indiceSummaryProto = parsedObject.indiceSummary

                    if (indexCode == indiceSummaryProto.indiceCode) {
                        withContext(Dispatchers.Main) {
                            getIndiceDataResult.postValue(
                                IndiceData(
                                    indiceSummaryProto.indiceCode,
                                    indiceSummaryProto.indiceVal,
                                    indiceSummaryProto.close,
                                    indiceSummaryProto.open,
                                    indiceSummaryProto.high,
                                    indiceSummaryProto.low,
                                    indiceSummaryProto.change,
                                    indiceSummaryProto.chgPercent,
                                    indiceSummaryProto.mktTradeInfo.marketVal,
                                    indiceSummaryProto.mktTradeInfo.marketVol,
                                    indiceSummaryProto.mktTradeInfo.marketFreq,
                                    indiceSummaryProto.mktTradeInfo.totalUp,
                                    indiceSummaryProto.mktTradeInfo.totalNoChange,
                                    indiceSummaryProto.mktTradeInfo.totalDown,
                                    indiceSummaryProto.mktTradeInfo.totalUnTrade
                                )
                            )
                        }
                    } else if (indexMap.containsKey(indiceSummaryProto.indiceCode)) {
                        val id = indexMap[indiceSummaryProto.indiceCode]?.id
                        val name = indexMap[indiceSummaryProto.indiceCode]?.indexName
                        indexMap[indiceSummaryProto.indiceCode] = IndexSectorDetailData(
                            id!!,
                            indiceSummaryProto.indiceCode,
                            indiceSummaryProto.indiceVal,
                            indiceSummaryProto.change,
                            indiceSummaryProto.chgPercent,
                            indexName = name ?: ""
                        )
                        withContext(Dispatchers.Main) {
                            getIndexDetailDataResult.postValue(indexMap.values.sortedBy { it.indiceCode })
                        }
                    } else if (sectorMap.containsKey(indiceSummaryProto.indiceCode)) {
                        val id = sectorMap[indiceSummaryProto.indiceCode]?.id
                        val stockCount = sectorMap[indiceSummaryProto.indiceCode]?.stockCount
                        val name = indexMap[indiceSummaryProto.indiceCode]?.indexName
                        sectorMap[indiceSummaryProto.indiceCode] = IndexSectorDetailData(
                            id!!,
                            indiceSummaryProto.indiceCode,
                            indiceSummaryProto.indiceVal,
                            indiceSummaryProto.change,
                            indiceSummaryProto.chgPercent,
                            stockCount!!,
                            indiceSummaryProto.indiceCode.GET_IMAGE_SECTOR(),
                            indexName = name ?: ""
                        )
                        val sortedSector = sectorMap.values.sortedBy { item -> item.indiceCode }
                        withContext(Dispatchers.Main) {
                            getSectorDetailDataResult.postValue(sortedSector)
                        }
                    }
                }
            } catch (ignore: Exception) {}
        }
    }

}