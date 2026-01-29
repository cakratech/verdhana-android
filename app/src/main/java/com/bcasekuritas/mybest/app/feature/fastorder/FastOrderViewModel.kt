package com.bcasekuritas.mybest.app.feature.fastorder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.OrderReplyObject
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.domain.dto.request.AmendFastOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.CancelFastOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.FastOrderListReq
import com.bcasekuritas.mybest.app.domain.dto.request.LogoutReq
import com.bcasekuritas.mybest.app.domain.dto.request.MarketSessionReq
import com.bcasekuritas.mybest.app.domain.dto.request.MaxOrderByStockReq
import com.bcasekuritas.mybest.app.domain.dto.request.PublishFastOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.SendOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockOrderbookRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockPosRequest
import com.bcasekuritas.mybest.app.domain.dto.response.FastOrderListInfo
import com.bcasekuritas.mybest.app.domain.dto.response.OltOrder
import com.bcasekuritas.mybest.app.domain.interactors.DeleteSessionsDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetFastOrderListUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetIpAddressUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetLogoutUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetMarketSessionUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetMaxOrderByStockUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSessionPinDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSimplePortfolioUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockOrderBookUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockPosUseCase
import com.bcasekuritas.mybest.app.domain.interactors.PublishFastOrderUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SendAllWithdrawFastOrderUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SendAmendFastOrderUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SendOrderUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SendWithdrawFastOrderUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SetListenerOrderBookUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeOrderBookListUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnSubscribeOrderBookListUseCase
import com.bcasekuritas.mybest.app.domain.repositories.OrderRepo
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.event.SingleLiveEvent
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.bcas.AccStockPosResponse
import com.bcasekuritas.rabbitmq.proto.bcas.FastOrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.LogoutResponse
import com.bcasekuritas.rabbitmq.proto.bcas.MarketSessionInfo
import com.bcasekuritas.rabbitmq.proto.bcas.MaxOrderByStockResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimplePortofolioResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.datafeed.MIType
import com.bcasekuritas.rabbitmq.proto.datafeed.OrderbookSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FastOrderViewModel @Inject constructor(
    private val getOrderBookUseCase: GetStockOrderBookUseCase,
    private val setListenerOrderBookUseCase: SetListenerOrderBookUseCase,
    private val subscribeOrderbookListUseCase: SubscribeOrderBookListUseCase,
    private val unSubscribeOrderbookListUseCase: UnSubscribeOrderBookListUseCase,
    private val publishFastOrderUseCase: PublishFastOrderUseCase,
    private val getFastOrderListUseCase: GetFastOrderListUseCase,
    private val sendOrderUseCase: SendOrderUseCase,
    private val getStockParamDaoUseCase: GetStockParamDaoUseCase,
    private val sendWithdrawUseCase: SendWithdrawFastOrderUseCase,
    private val sendAllWithdrawUseCase: SendAllWithdrawFastOrderUseCase,
    private val sendAmendUseCase: SendAmendFastOrderUseCase,
    private val getStockPosUseCase: GetStockPosUseCase,
    private val getMaxOrderByStockUseCase: GetMaxOrderByStockUseCase,
    private val getSessionPin: GetSessionPinDaoUseCase,
    private val getSimplePortfolioUseCase: GetSimplePortfolioUseCase,
    private val orderRepo: OrderRepo,
    private val logoutUseCase: GetLogoutUseCase,
    private val deleteSessions: DeleteSessionsDaoUseCase,
    private val getMarketSessionUseCase: GetMarketSessionUseCase,
    private val getIpAddressUseCase: GetIpAddressUseCase
): BaseViewModel() {

    val getStockOrderbookResult = MutableLiveData<List<OrderbookSummary?>>()
    val getFastOrderListResult = MutableLiveData<Resource<FastOrderListResponse?>>()
    var getStockParamResult = MutableLiveData<StockParamObject?>()
    val getPinSessionResult = SingleLiveEvent<Resource<Long?>>()
    val getMaxOrderByStockResult = MutableLiveData<Resource<MaxOrderByStockResponse?>>()
    val getStockPosResult = MutableLiveData<Resource<AccStockPosResponse?>>()
    val getSimplePortfolioResult = MutableLiveData<Resource<SimplePortofolioResponse?>>()
    val getLogoutResult = MutableLiveData<LogoutResponse?>()
    val getMarketSessionResult = MutableLiveData<MarketSessionInfo>()
    val getIpAddressResult = MutableLiveData<String>()

    val fastOrderLiveData: LiveData<FastOrderListInfo>
        get() = orderRepo.fastOrderLiveData

    val orderReplyLiveData: LiveData<OltOrder?>
        get() = orderRepo.orderReplyLiveData as MutableLiveData

    fun clearOrderReply() {
        (orderReplyLiveData as MutableLiveData).value = null
    }

    private var stockCodes = ""

    fun getSessionPin(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getSessionPin.invoke(userId).collect() { resource ->
                resource.let {
                    getPinSessionResult.postValue(it)
                }
            }
        }
    }

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

//    fun getSimplePortfolio(userId: String, sessionId: String, accNo: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val simplePortfolio = SessionRequest(userId, sessionId, accNo)
//
//            getSimplePortfolioUseCase.invoke(simplePortfolio).collect() {resource ->
//                resource.let {
//                    withContext(Dispatchers.Main) {
//                        getSimplePortfolioResult.postValue(it)
//                    }
//                }
//            }
//        }
//    }

    fun getStockOrderbook(userId: String, userSession: String, stockCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val stockCodeList = listOf(stockCode)
            stockCodes = stockCode

            val stockOrderbookRequest =
                StockOrderbookRequest(userId, userSession, "RG", stockCodeList)

            getOrderBookUseCase.invoke(stockOrderbookRequest).collect() { resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            it.data?.let { res ->
                                subscribeOrderbook(res[0]!!.secCode)
                                getStockOrderbookResult.postValue(res)
                            }
                        }

                        else -> {}
                    }
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

    fun setListenerOrderBook() {
        viewModelScope.launch(Dispatchers.IO) {
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

    fun getFastOrderList(userId: String, sessionId: String, accNo: String, stockCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val fastOrderListReq = FastOrderListReq(
                userId = userId,
                accNo = accNo,
                sessionId = sessionId,
                stockCode = stockCode)

            getFastOrderListUseCase.invoke(fastOrderListReq).collect() {resource ->
                resource.let {
                    withContext(Dispatchers.Main) {
                        getFastOrderListResult.postValue(it)
                    }
                }
            }
        }
    }

    fun publishFastOrder(userId: String, sessionId: String, subsOp: Int, accNo: String, stockCode: String){
        viewModelScope.launch( Dispatchers.IO ) {
            val publishFastOrder = PublishFastOrderReq(userId, sessionId, subsOp, accNo, stockCode)

            publishFastOrderUseCase.publishFastOrderRepo(publishFastOrder)
        }
    }

    fun getMaxOrder(userId: String, accNo: String, buySell: String, sessionId: String, stockCode: String,
                    price: Double, compare: String, boardCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val maxOrderByStockReq = MaxOrderByStockReq(
                userId = userId,
                accNo = accNo,
                buySell = buySell,
                stockCode = stockCode,
                price = price,
                buyType = compare,
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

    /** Send Order*/

    fun sendOrder(sendOrder: SendOrderReq) {
        viewModelScope.launch(Dispatchers.IO) {
            sendOrderUseCase.sendOrder(sendOrder)
        }
    }

    fun sendWithdraw(cancelFastOrderReq: CancelFastOrderReq) {
        viewModelScope.launch(Dispatchers.IO) {
            sendWithdrawUseCase.sendWithdrawFastOrder(cancelFastOrderReq)
        }
    }

    fun sendAllWithdraw(cancelFastOrderReq: CancelFastOrderReq) {
        viewModelScope.launch(Dispatchers.IO) {
            sendAllWithdrawUseCase.sendAllWithdrawFastOrder(cancelFastOrderReq)
        }
    }

    fun sendAmend(amendFastOrderReq: AmendFastOrderReq) {
        viewModelScope.launch(Dispatchers.IO) {
            sendAmendUseCase.sendAmendFastOrder(amendFastOrderReq)
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

    private val miListener = MQMessageListener<MIMessage> { result ->
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("orderBookRealtime", "get realtime data")
            val parsedObject = result.protoMsg
            if (parsedObject.type == MIType.ORDERBOOK_SUMMARY_COMPACT) {
                val orderbookSummaryProto = parsedObject.orderbookSummary
                withContext(Dispatchers.Main) {
                    getStockOrderbookResult.postValue(arrayListOf(orderbookSummaryProto))
                }
            }
        }
    }
}