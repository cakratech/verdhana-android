package com.bcasekuritas.mybest.app.feature.order.conditionadvanced

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.AccountObject
import com.bcasekuritas.mybest.app.data.entity.StockNotationObject
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.data.mapper.toTradeSummary
import com.bcasekuritas.mybest.app.domain.dto.request.AdvanceOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.LogoutReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockPosRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.response.TradeSummary
import com.bcasekuritas.mybest.app.domain.interactors.DeleteSessionsDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetAccountInfoDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetIpAddressUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetLogoutUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetNotationByStockCodeDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSessionPinDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockDetailUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockPosUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SendAdvOrderBuyUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SetListenerTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnSubscribeTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.converter.tryDeserialize
import com.bcasekuritas.mybest.ext.event.SingleLiveEvent
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.message.MQMessageEvent
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.bcas.AccStockPosResponse
import com.bcasekuritas.rabbitmq.proto.bcas.LogoutResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.datafeed.MIType
import com.google.protobuf.GeneratedMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ConditionAdvancedViewModel @Inject constructor(
    private val getStockPosUseCase: GetStockPosUseCase,
    private val getStockDetailUseCase: GetStockDetailUseCase,
    private val sendAdvOrderBuyUseCase: SendAdvOrderBuyUseCase,
    private val getSessionPin: GetSessionPinDaoUseCase,
    private val getStockParamDaoUseCase: GetStockParamDaoUseCase,
    private val getAccountInfoDaoUseCase: GetAccountInfoDaoUseCase,
    private val setListenerTradeSumUseCase: SetListenerTradeSumUseCase,
    private val subscribeTradeSumUseCase: SubscribeTradeSumUseCase,
    private val unSubscribeTradeSumUseCase: UnSubscribeTradeSumUseCase,
    val imqConnectionListener: IMQConnectionListener,
    private val logoutUseCase: GetLogoutUseCase,
    private val deleteSessions: DeleteSessionsDaoUseCase,
    private val getNotationByStockCodeDaoUseCase: GetNotationByStockCodeDaoUseCase,
    private val getIpAddressUseCase: GetIpAddressUseCase
): BaseViewModel() {

    val getStockPosResult = MutableLiveData<Resource<AccStockPosResponse?>>()
    var getStockDetailResult = MutableLiveData<TradeSummary?>()
    val getPinSessionResult = SingleLiveEvent<Resource<Long?>>()
    var getStockParamResult = MutableLiveData<StockParamObject?>()
    var getAccountInfoResult = MutableLiveData<AccountObject?>()
    val getSubscribeTradeSummary = MutableLiveData<TradeSummary?>()
    val getLogoutResult = MutableLiveData<LogoutResponse?>()
    var getStockNotationResult = MutableLiveData<List<StockNotationObject?>>()
    val getIpAddressResult = MutableLiveData<String>()

    private var secCode = ""

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

    fun getStockParam(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getStockParamDaoUseCase(value).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { data ->
                            getStockParamResult.postValue(data)
                        }
                    }

                    else -> {
                        // Handle other cases if needed
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

    fun getStockDetail(userId: String, userSession: String, stockCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            secCode = stockCode
            val stockCodeReq = StockWatchListRequest(userId, userSession, "RG", listOf(stockCode))

            getStockDetailUseCase.invoke(stockCodeReq).collect() { resource ->
                resource.let { res ->
                    when (res) {
                        is Resource.Success -> {
                            if (res.data != null) {
                                when (res.data.status) {
                                    0 -> {
                                        if (res.data.curMsgInfoCount > 0) {
                                            val data = res.data.curMsgInfoList?.toTradeSummary()?.get(0)
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
                                        }
                                    }
                                    else -> {}
                                }
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    fun sendAdvOrder(advOrder: AdvanceOrderRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            sendAdvOrderBuyUseCase.sendAdvOrderBuy(advOrder)
        }
    }

    fun getSessionPin(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getSessionPin.invoke(userId).collect() { resource ->
                resource.let {
                    getPinSessionResult.postValue(it)
                }
            }
        }
    }

    fun setListenerTradeSummary() {
        viewModelScope.launch(Dispatchers.IO) {
            setListenerTradeSumUseCase.setListener(miListener)
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
}