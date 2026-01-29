package com.bcasekuritas.mybest.app.feature.pricealert.createedit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.StockNotationObject
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.data.mapper.toTradeSummary
import com.bcasekuritas.mybest.app.domain.dto.request.AddPriceAlertReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.response.TradeSummary
import com.bcasekuritas.mybest.app.domain.interactors.AddPriceAlertUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetNotationByStockCodeDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockDetailUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SearchStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SetListenerTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnSubscribeTradeSumUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.converter.tryDeserialize
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.message.MQMessageEvent
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.bcas.AddPriceAlertResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.datafeed.MIType
import com.google.protobuf.GeneratedMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CreateEditPriceAlertViewModel @Inject constructor(
    private val getStockParamDaoUseCase: GetStockParamDaoUseCase,
    private val getNotationByStockCodeDaoUseCase: GetNotationByStockCodeDaoUseCase,
    private val searchStockParamDaoUseCase: SearchStockParamDaoUseCase,
    private val addPriceAlertUseCase: AddPriceAlertUseCase,
    private val getStockDetailUseCase: GetStockDetailUseCase,
    private val setListenerTradeSumUseCase: SetListenerTradeSumUseCase,
    private val subscribeTradeSumUseCase: SubscribeTradeSumUseCase,
    private val unSubscribeTradeSumUseCase: UnSubscribeTradeSumUseCase,
    val imqConnectionListener: IMQConnectionListener
): BaseViewModel() {

    val addPriceAlertResult = MutableLiveData<AddPriceAlertResponse?>()
    val getStockDetailResult = MutableLiveData<TradeSummary?>()
    var getStockParamResult = MutableLiveData<StockParamObject?>()
    var getStockNotationResult = MutableLiveData<List<StockNotationObject?>>()
    var getAllStockParamResult = MutableLiveData<List<StockParamObject?>>()
    val getSubscribeTradeSummary = MutableLiveData<TradeSummary?>()

    private var secCode = ""

    fun addPriceAlert(userId: String, sessionId: String, stockCode: String, price: Double) {
        viewModelScope.launch {
            val addPriceAlertReq = AddPriceAlertReq(
                userId,
                sessionId,
                stockCode,
                "=",
                price,
                0
            )

            addPriceAlertUseCase.invoke(addPriceAlertReq).collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        if (resource.data != null) {
                            addPriceAlertResult.postValue(resource.data)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun editPriceAlert(userId: String, sessionId: String, stockCode: String, price: Double, id: Long) {
        viewModelScope.launch {
            val editPriceAlertReq = AddPriceAlertReq(
                userId,
                sessionId,
                stockCode,
                "=",
                price,
                id
            )

            addPriceAlertUseCase.invoke(editPriceAlertReq).collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        if (resource.data != null) {
                            addPriceAlertResult.postValue(resource.data)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun getStockDetailSummary(userId: String, userSession: String, stockCode: String, isRefresh: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            secCode = stockCode
            val stockCodeReq = StockWatchListRequest(userId,userSession, "RG", listOf(stockCode))

            getStockDetailUseCase.invoke(stockCodeReq).collect() { resource ->
                resource.let {res ->
                    when (res) {
                        is Resource.Success -> {
                            if (res.data != null) {
                                when (res.data.status) {
                                    0 -> {
                                        if (res.data.curMsgInfoCount > 0) {
                                            val data = res.data.curMsgInfoList.toTradeSummary().get(0)
                                            getStockDetailResult.postValue(data)
                                            if (!isRefresh) {
                                                subscribeTradeSummary(stockCode)
                                            }
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

    fun getStockParam(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getStockParamDaoUseCase(value).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { data ->
                            withContext(Dispatchers.Main){
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

    fun getAllStockParam(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val resultList = mutableListOf<StockParamObject?>()
            searchStockParamDaoUseCase(value).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { data ->
                            resultList.addAll(data)
                            getAllStockParamResult.postValue(resultList.toList())
                        }
                    }

                    else -> {
                        // Handle other cases if needed
                    }
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

}