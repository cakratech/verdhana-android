package com.bcasekuritas.mybest.app.feature.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.StockNotationObject
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.data.mapper.toTradeSummary
import com.bcasekuritas.mybest.app.domain.dto.request.AllUserWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.LogoutReq
import com.bcasekuritas.mybest.app.domain.dto.request.OrderListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.PromoBannerReq
import com.bcasekuritas.mybest.app.domain.dto.request.PublishAccPosReq
import com.bcasekuritas.mybest.app.domain.dto.request.SessionRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockParamListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.UserWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.response.TradeSummary
import com.bcasekuritas.mybest.app.domain.dto.response.TradeSummaryItem
import com.bcasekuritas.mybest.app.domain.interactors.DeleteNotationByCodeDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.DeleteSessionsDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.DeleteStockByCodeDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetAccNameDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetAllStockNotationDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetAllStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetListStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetLogoutUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetOrderListUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetPromoBannerUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSessionPinDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSimpleAllWatchlistUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSimplePortfolioUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSimpleWatchlistUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockDetailUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockParamListUseCase
import com.bcasekuritas.mybest.app.domain.interactors.InsertAllStockNotationDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.InsertAllStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.PublishAccPosUseCase
import com.bcasekuritas.mybest.app.domain.interactors.RemoveItemCategoryUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SetListenerCIFStockPosUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SetListenerTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeAllTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeCIFStockPosUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnSubscribeAllTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnsubscribeCIFStockPosUseCase
import com.bcasekuritas.mybest.app.domain.repositories.OrderRepo
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.common.getCurrentTimeInMillis
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.bcas.CakraMessage
import com.bcasekuritas.rabbitmq.proto.bcas.LogoutResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RemoveUserWatchListItemResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleAllUserWatchListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimplePortofolio
import com.bcasekuritas.rabbitmq.proto.bcas.SimplePortofolioResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleUserWatchListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.StockParamInfo
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.datafeed.MIType
import com.bcasekuritas.rabbitmq.proto.news.PromotionBannerResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getStockDetailUseCase: GetStockDetailUseCase,
    private val setListenerTradeSumUseCase: SetListenerTradeSumUseCase,
    private val subscribeAllTradeSumUseCase: SubscribeAllTradeSumUseCase,
    private val unSubscribeAllTradeSumUseCase: UnSubscribeAllTradeSumUseCase,
    private val getSimpleWatchlistUseCase: GetSimpleWatchlistUseCase,
    private val getSimpleAllWatchlistUseCase: GetSimpleAllWatchlistUseCase,
    private val getSessionPin: GetSessionPinDaoUseCase,

    private val getAllStockParamDaoUseCase: GetAllStockParamDaoUseCase, // Get All stock param
    private val getAllStockNotationDaoUseCase: GetAllStockNotationDaoUseCase, // Get All stock notation
    private val getListStockParamDaoUseCase: GetListStockParamDaoUseCase, // Get stock param by list stock

    private val insertAllStockParamUseCase: InsertAllStockParamDaoUseCase,
    private val insertAllStockNotatioUseCase: InsertAllStockNotationDaoUseCase,
    private val deleteStockByCodeDaoUseCase: DeleteStockByCodeDaoUseCase,
    private val deleteNotationByCodeDaoUseCase: DeleteNotationByCodeDaoUseCase,

    private val getStockParamListUseCase: GetStockParamListUseCase,
    private val getSimplePortfolioUseCase: GetSimplePortfolioUseCase,
    private val getOrderListUseCase: GetOrderListUseCase,
    private val getAccNameDaoUseCase: GetAccNameDaoUseCase,
    private val orderRepo: OrderRepo,
    private val removeItemCategoryUseCase: RemoveItemCategoryUseCase,
    private val getPromoBannerUseCase: GetPromoBannerUseCase,
    val imqConnectionListener: IMQConnectionListener,
    private val logoutUseCase: GetLogoutUseCase,
    private val deleteSessions: DeleteSessionsDaoUseCase,
    private val setListenerCIFStockPosUseCase: SetListenerCIFStockPosUseCase,
    private val subscribeCIFStockPosUseCase: SubscribeCIFStockPosUseCase,
    private val unsubscribeCIFStockPosUseCase: UnsubscribeCIFStockPosUseCase,
    private val publishAccPosUseCase: PublishAccPosUseCase
) : BaseViewModel() {

    val getSimplePortfolioResult = MutableLiveData<Resource<SimplePortofolioResponse?>>()
    val getStockDetailWatchList = MutableLiveData<List<TradeSummary?>>()
    val getSimpleWatchlistResult = MutableLiveData<Resource<SimpleUserWatchListResponse?>>()
    val getSimpleAllWatchlistResult = MutableLiveData<SimpleAllUserWatchListResponse?>()
    val getSessionPinResult = MutableLiveData<Long?>()
    val getAccNameDaoResult = MutableLiveData<String>()
    val getPromoBannerResult = MutableLiveData<Resource<PromotionBannerResponse?>>()
    val removeItemCategoryResult = MutableLiveData<Resource<RemoveUserWatchListItemResponse?>>()
    val showSessionExpired = MutableLiveData<Boolean>()
    val getLogoutResult = MutableLiveData<LogoutResponse?>()
    val getRealtimeWatchlistPortfolioResult = MutableLiveData<Resource<SimpleUserWatchListResponse?>>()
    val getRealtimeSimplePortfolioResult by lazy { MutableLiveData<SimplePortofolio>() }
    val getRealtimeStockDetailWatchList by lazy { MutableLiveData<List<TradeSummary?>>() }
    val getSubscribeStockWatchlist = MutableLiveData<TradeSummary>()

    private val tradeSummaryMap = mutableMapOf<String, TradeSummary>()
    private val latestRoutingKey = mutableListOf<String>()

    val getOrderMatchLiveData: LiveData<String>
        get() = orderRepo.getOrderMatch

    fun getSimplePortfolio(userId: String, sessionId: String, accNo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val simplePortfolio = SessionRequest(userId, sessionId, accNo)

            getSimplePortfolioUseCase.invoke(simplePortfolio).collect() { resource ->
                resource.let {
                    getSimplePortfolioResult.postValue(it)
                }
            }
        }
    }

    fun getPromoBannerUseCase(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val promoBannerReq = PromoBannerReq(userId, sessionId)

            getPromoBannerUseCase.invoke(promoBannerReq).collect() { resource ->
                resource.let {
                    getPromoBannerResult.postValue(it)
                }
            }
        }
    }

    fun getSimpleWatchlist(userId: String, sessionId: String, wlgCode: String? = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val watchListRequest =
                UserWatchListRequest(
                    userId, sessionId = sessionId, wlgCode
                ) //if wlg empty = get all watchlist

            getSimpleWatchlistUseCase.invoke(watchListRequest).collect() { resource ->
                resource.let {
                    if (wlgCode?.isNotEmpty() == true) {
                        getRealtimeWatchlistPortfolioResult.postValue(it)
                    } else {
                        getSimpleWatchlistResult.postValue(it)
                    }
                }
            }
        }
    }

    fun getAllWatchlist(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val watchlistRequest = AllUserWatchListRequest(userId, sessionId)

            getSimpleAllWatchlistUseCase.invoke(watchlistRequest).collect() { resource ->
                resource.let {
                    when (resource) {
                        is Resource.Success -> {
                            getSimpleAllWatchlistResult.postValue(resource.data)
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    fun getStockDetail(
        userId: String,
        userSession: String,
        wlCode: String,
        stockCodeList: List<String>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            tradeSummaryMap.clear()
            val stockCodeReq = StockWatchListRequest(userId, userSession, "RG", stockCodeList)

            if (stockCodeList.isNotEmpty()) {
                stockCodeList.forEach {stockCode ->
                    if (stockCode.isNotEmpty()) {
                        tradeSummaryMap[stockCode] = TradeSummary(secCode = stockCode)
                    }
                }
            }

            getStockDetailUseCase.invoke(stockCodeReq).collect() { resource ->
                resource.let { res ->
                    when (res) {
                        is Resource.Success -> {
                            if (res.data != null) {
                                when (res.data.status) {
                                    0 -> {
                                        // 🔹 Fix: Copy list before iterating to avoid ConcurrentModificationException
                                        if (latestRoutingKey.isNotEmpty()) {
                                            unSubscribeTradeSummary()
                                        }

                                        val data = res.data.curMsgInfoList.toTradeSummary()
                                        data.forEach { tradeSummary ->
                                            when (wlCode) {
                                                "All" -> tradeSummary.type = TradeSummaryItem.TYPE_ALL
                                                "Portfolio" -> tradeSummary.type = TradeSummaryItem.TYPE_PORT
                                                else -> tradeSummary.type = TradeSummaryItem.TYPE_CAT
                                            }
                                            tradeSummaryMap[tradeSummary.secCode] = tradeSummary
                                        }

                                        val routingKey = tradeSummaryMap.keys.toList()

                                        latestRoutingKey.clear()
                                        latestRoutingKey.addAll(routingKey)

                                        getStockDetailWatchList.postValue(ArrayList(tradeSummaryMap.values))
                                        getListStockParam(routingKey)

                                        if (routingKey.isNotEmpty()) {
                                            subscribeTradeSummary()
                                        }
                                    }

                                    2 -> showSessionExpired.postValue(true)
                                }

                            } else {
                                // 🔹 Fix: Copy list before iterating
                                if (latestRoutingKey.isNotEmpty()) {
                                    unSubscribeTradeSummary()
                                }
                                if (stockCodeList.isNotEmpty()) {
                                    stockCodeList.forEach { stockCode ->
                                        tradeSummaryMap[stockCode] = TradeSummary(secCode = stockCode)
                                    }
                                    getListStockParam(stockCodeList)
                                }
                            }
                        }

                        else -> {}
                    }
                }
            }

        }
    }

    private fun getListStockParam(value: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            getListStockParamDaoUseCase(value).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { data ->
                            data.map { item ->
                                val listNotation = item?.stockNotation?.map { it.notation }
                                tradeSummaryMap[item?.stockParam?.stockCode]?.let {
                                    if (item != null) {
                                        it.stockName = item.stockParam.stockName
                                        if (listNotation != null) {
                                            it.notation = listNotation.joinToString()
                                        }
                                    }
                                }
                            }
                            getStockDetailWatchList.postValue(ArrayList(tradeSummaryMap.values))
                        }
                    }

                    else -> {
                        // Handle other cases if needed
                    }
                }
            }
        }
    }

    private fun deleteStockAndNotations(stockCodeList: List<String>, notationList: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteStockByCodeDaoUseCase.delete(stockCodeList).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                    }
                    else -> {}
                }
            }
            deleteNotationByCodeDaoUseCase.delete(notationList).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                    }
                    else -> {}
                }
            }
        }
    }

    fun removeItemWatchlist(
        userId: String,
        wlCode: String,
        itemCode: String,
        itemSeq: Int,
        sessionId: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val watchListRequest = UserWatchListRequest(
                userId,
                wlCode = wlCode,
                itemCode = itemCode,
                itemSeq = itemSeq,
                sessionId = sessionId
            )
            unSubscribeAllTradeSumUseCase.unSubscribe(listOf("RG.$itemCode"))
            removeItemCategoryUseCase.invoke(watchListRequest).collect() { resource ->
                resource.let {
                    removeItemCategoryResult.postValue(it)
                }
            }
        }
    }

    fun setListenerRealtimeData() {
        viewModelScope.launch(Dispatchers.IO) {
            setListenerTradeSumUseCase.setListener(miListener)
            setListenerCIFStockPosUseCase.setListenerCIFStockPos(cakraListener)
        }
    }

    private fun subscribeTradeSummary() {
        viewModelScope.launch(Dispatchers.IO) {
            val keysToSubscribe = latestRoutingKey.toList().map { "RG.$it" }
            subscribeAllTradeSumUseCase.subscribe(keysToSubscribe)
        }
    }

    fun unSubscribeTradeSummary() {
        viewModelScope.launch(Dispatchers.IO) {
            val keysToUnsubscribe = latestRoutingKey.toList().map { "RG.$it" }
            unSubscribeAllTradeSumUseCase.unSubscribe(keysToUnsubscribe)
        }
    }

    fun getAccNameDao(accNo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getAccNameDaoUseCase.invoke(accNo).collect() {
                when (it) {
                    is Resource.Success -> {
                        getAccNameDaoResult.postValue(it.data ?: "")
                    }

                    else -> {}
                }
            }
        }
    }

    fun getSessionPin(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            getSessionPin.invoke(userId).collect() { resource ->
                when (resource) {
                    is Resource.Success -> {
                        getSessionPinResult.postValue(resource.data)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun insertAllStockData(
        stockParamRes: List<StockParamObject>,
        stockNotationRes: List<StockNotationObject>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val stockInsertDeferred = async {
                insertAllStockParamUseCase.insertAllStockParamDao(stockParamRes).firstOrNull()
            }
            val notationInsertDeferred = async {
                insertAllStockNotatioUseCase.insertAllStockNotationDao(stockNotationRes).firstOrNull()
            }

            val (stockInsertResult, notationInsertResult) = awaitAll(stockInsertDeferred, notationInsertDeferred)

            if ((stockInsertResult is Resource.Success || notationInsertResult is Resource.Success) && tradeSummaryMap.isNotEmpty()) {
                val stockCodeList = tradeSummaryMap.keys.toList()
                getListStockParam(stockCodeList)
            }
        }
    }

    private fun updateStockData(listStockParam: List<StockParamInfo>) {
        viewModelScope.launch(Dispatchers.IO) {
            val localStockList = mutableListOf<StockParamObject>()
            val localNotationList = mutableListOf<StockNotationObject>()

            // Collect stock and notation data concurrently
            val job1 = launch {
                getAllStockParamDaoUseCase.invoke().collectLatest { resource ->
                    if (resource is Resource.Success) {
                        localStockList.addAll(resource.data ?: emptyList())
                    }
                }
            }
            val job2 = launch {
                getAllStockNotationDaoUseCase.invoke().collectLatest { resource ->
                    if (resource is Resource.Success) {
                        localNotationList.addAll(resource.data ?: emptyList())
                    }
                }
            }

            job1.join()
            job2.join()

            val remoteStockMap = listStockParam.associateBy { it.stockcode }
            val localStockMap = localStockList.associateBy { it.stockCode }
            val localNotationMap = localNotationList.groupBy { it.stockCode }

            val stockToInsertOrUpdate = mutableListOf<StockParamObject>()
            val stockToDelete = mutableListOf<String>()
            val notationToInsertOrUpdate = mutableListOf<StockNotationObject>()
            val notationToDelete = mutableListOf<String>()

            // Identify stocks to delete
            localStockList.forEach { localStock ->
                if (!remoteStockMap.containsKey(localStock.stockCode)) {
                    stockToDelete.add(localStock.stockCode)
                }
            }

            // Process remote stock data
            listStockParam.forEach { remoteStock ->
                val stockNotasi = StockParamInfo.newBuilder()
                    .setStockcode(remoteStock.stockcode)
                    .addAllStockNotasi(remoteStock.stockNotasiList)
                    .build().toByteArray()

                val stockObject = StockParamObject(
                    stockCode = remoteStock.stockcode ?: "",
                    stockName = remoteStock.stockname ?: "",
                    idxTrdBoard = remoteStock.idxTrdboard ?: "",
                    stockNotasi = stockNotasi,
                    hairCut = 100.0.minus(remoteStock.valpctReg)
                )

                stockToInsertOrUpdate.add(stockObject)

                // Handle notations (avoid duplicates)
                val newNotations = remoteStock.stockNotasiList.distinctBy { it.code }
                val existingNotations = localNotationMap[remoteStock.stockcode].orEmpty().associateBy { it.notation }

                newNotations.forEach { notation ->
                    if (!existingNotations.containsKey(notation.code)) {
                        notationToInsertOrUpdate.add(
                            StockNotationObject(
                                stockCode = remoteStock.stockcode ?: "",
                                notation = notation.code ?: "",
                                description = notation.description ?: ""
                            )
                        )
                    }
                }

                // Find outdated notations to delete
                existingNotations.keys.forEach { existingNotation ->
                    if (newNotations.none { it.code == existingNotation }) {
                        notationToDelete.add(existingNotation)
                    }
                }
            }

            // Perform Database Operations
            deleteStockAndNotations(stockToDelete, notationToDelete)
            insertAllStockData(stockToInsertOrUpdate, notationToInsertOrUpdate)
        }
    }

    fun getStockParamList(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            val stockParamReq = StockParamListRequest(
                userId,
                sessionId,
                "IDX",
                "*",
                getCurrentTimeInMillis()
            )
            getStockParamListUseCase.invoke(stockParamReq).collect() { resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            when (it.data?.status) {
                                0 -> {
                                    updateStockData(it.data.stockParamInfoList)
                                }
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }


    fun getOrderList(userId: String, accNo: String, sessionId: String, includeTrade: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val orderListRequest = OrderListRequest(userId, accNo, sessionId, includeTrade)

            getOrderListUseCase.invoke(orderListRequest).collect() { resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            when (it.data?.status) {
                                0 -> orderRepo.putOrderReply(it.data.ordersList)
                                2 -> showSessionExpired.postValue(true)
                            }
                        }

                        else -> {}
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

    private val cakraListener = MQMessageListener<CakraMessage> { result ->
        viewModelScope.launch(Dispatchers.IO) {
            val parsedObject = result.protoMsg
            if (parsedObject.type == CakraMessage.Type.SIMPLE_PORTOFOLIO_RESPONSE) {
                getRealtimeSimplePortfolioResult.postValue(parsedObject.simplePortofolioResponse.simplePortofolio)
            }
        }
    }

    private val miListener = MQMessageListener<MIMessage> { result ->
        viewModelScope.launch(Dispatchers.IO) {
            val parsedObject = result.protoMsg
            if (parsedObject.type == MIType.TRADE_SUMMARY) {
                val tradeSummaryProto = parsedObject.tradeSummary
                val stockCode = tradeSummaryProto.secCode

                if (tradeSummaryMap.containsKey(stockCode)) {
                    tradeSummaryMap[stockCode]?.let {
                        it.change = tradeSummaryProto.change
                        it.changePct = tradeSummaryProto.changePct
                        it.last = tradeSummaryProto.last
                        it.close = tradeSummaryProto.close

                        tradeSummaryMap[stockCode] = it
//                        getRealtimeStockDetailWatchList.postValue(ArrayList(tradeSummaryMap.values))
                    }
                    getSubscribeStockWatchlist.postValue(tradeSummaryMap[stockCode])
                }
            }
        }
    }

    fun startRealtimeData(userId: String, sessionId: String, accNo: String) {
        viewModelScope.launch {
            subsCifStockPos(accNo) // Waits for the second to complete, then executes
        }
    }

    fun stopRealtimeData(userId: String, sessionId: String, accNo: String) {
        viewModelScope.launch {
            unSubsCifStockPos(accNo)
        }
    }

    fun publishAccPos(userId: String, sessionId: String, subsOp: Int, accNo: String){
        viewModelScope.launch(Dispatchers.IO) {
            val publishAccPosReq = PublishAccPosReq(userId, sessionId, subsOp, accNo)

            publishAccPosUseCase.publishFastOrderRepo(publishAccPosReq)
//            Log.d("accPosRealtime", "simple portfolio publish: $subsOp")
        }

    }

    fun subsCifStockPos(accNo: String){
        viewModelScope.launch(Dispatchers.IO){
            subscribeCIFStockPosUseCase.subscribeCIFStockPos(accNo)
//            Log.d("accPosRealtime", "simple portfolio subscribe")
        }
    }

    fun unSubsCifStockPos(accNo: String){
        viewModelScope.launch(Dispatchers.IO){
            unsubscribeCIFStockPosUseCase.unsubscribeCIFStockPos(accNo)
//            Log.d("accPosRealtime", "simple portfolio unsubscribe")
        }
    }
}