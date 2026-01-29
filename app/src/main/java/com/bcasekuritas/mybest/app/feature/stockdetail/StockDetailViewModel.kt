package com.bcasekuritas.mybest.app.feature.stockdetail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.StockNotationObject
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.domain.dto.request.AccountInfoRequest
import com.bcasekuritas.mybest.app.domain.dto.request.ChartIntradayRequest
import com.bcasekuritas.mybest.app.domain.dto.request.LogoutReq
import com.bcasekuritas.mybest.app.domain.dto.request.MarketSessionReq
import com.bcasekuritas.mybest.app.domain.dto.request.PriceAlertReq
import com.bcasekuritas.mybest.app.domain.dto.request.PublishAccPosReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockInfoDetailRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockPosRequest
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioStockDataItem
import com.bcasekuritas.mybest.app.domain.interactors.DeleteSessionsDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetAccountInfoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetChartIntradayPriceUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetListPriceAlertUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetLogoutUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetMarketSessionUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetNotationByStockCodeDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSessionPinDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockInfoDetailUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockPosUseCase
import com.bcasekuritas.mybest.app.domain.interactors.PublishAccPosUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SearchStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SetListenerCIFStockPosUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeCIFStockPosUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnsubscribeCIFStockPosUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.converter.tryDeserialize
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.message.MQMessageEvent
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.bcas.AccStockPosResponse
import com.bcasekuritas.rabbitmq.proto.bcas.CakraMessage
import com.bcasekuritas.rabbitmq.proto.bcas.LogoutResponse
import com.bcasekuritas.rabbitmq.proto.bcas.MarketSessionInfo
import com.bcasekuritas.rabbitmq.proto.chart.Cf
import com.bcasekuritas.rabbitmq.proto.news.ViewStockInfoDetilResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StockDetailViewModel @Inject constructor(
    private val getAccountInfoUseCase: GetAccountInfoUseCase,
    private val getStockPosUseCase: GetStockPosUseCase,
    private val getStockParamDaoUseCase: GetStockParamDaoUseCase,
    private val searchStockParamDaoUseCase: SearchStockParamDaoUseCase,
    private val getSessionPin: GetSessionPinDaoUseCase,
    private val getNotationByStockCodeDaoUseCase: GetNotationByStockCodeDaoUseCase,
    private val getStockInfoDetailUseCase: GetStockInfoDetailUseCase,
    private val chartIntradayPriceUseCase: GetChartIntradayPriceUseCase,
    private val getListPriceAlertUseCase: GetListPriceAlertUseCase,
    private val getMarketSessionUseCase: GetMarketSessionUseCase,
    val imqConnectionListener: IMQConnectionListener,
    private val logoutUseCase: GetLogoutUseCase,
    private val deleteSessions: DeleteSessionsDaoUseCase,
    private val setListenerCIFStockPosUseCase: SetListenerCIFStockPosUseCase,
    private val subscribeCIFStockPosUseCase: SubscribeCIFStockPosUseCase,
    private val unsubscribeCIFStockPosUseCase: UnsubscribeCIFStockPosUseCase,
    private val publishAccPosUseCase: PublishAccPosUseCase
): BaseViewModel() {

    val getStockPosResult = MutableLiveData<Resource<AccStockPosResponse?>>()
    var getStockParamResult = MutableLiveData<StockParamObject?>()
    var getAllStockParamResult = MutableLiveData<List<StockParamObject?>>()
    var getSessionPinResult = MutableLiveData<Long?>()
    var getStockNotationResult = MutableLiveData<List<StockNotationObject?>>()
    var getStockInfoDetailResult = MutableLiveData<Resource<ViewStockInfoDetilResponse?>>()
    val getChartIntradayResult = MutableLiveData<List<Cf.CFMessage.IntradayPriceInfo>>()
    val getMarketSessionResult = MutableLiveData<MarketSessionInfo>()
    val isPriceAlertEmpty = MutableLiveData<Boolean?>()
    val getLogoutResult = MutableLiveData<LogoutResponse?>()
    val getRealtimeStockPos = MutableLiveData<PortfolioStockDataItem>()

    private var stockCode = ""

    fun setStockCode(code: String) {
        stockCode = code
    }

    // Tidak Dipakai
    fun getAccountInfo(userId: String, sessionId: String, cifCode: String, stockCode: String) {
        viewModelScope.launch(Dispatchers.IO) {

            val accountInfoRequest = AccountInfoRequest(userId, cifCode, 1, sessionId)

            getAccountInfoUseCase.invoke(accountInfoRequest).collect() {resource ->
                resource?.let {
                    when (it) {
                        is Resource.Success -> {
                            val accno = it.data?.cifInfo?.accountGroupList?.get(0)?.accountinfoList?.get(0)?.accNo
                            if (accno != null) {
                                getStockPos(userId, accno, sessionId, stockCode)
                            }
                        } else -> {}
                    }
                }
            }
        }
    }

    fun getChartIntraday(userId: String, sessionId: String, itemCode: String, ssDateFrom: Long, ssDateTo: Long, timeUnit: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val chartRequest = ChartIntradayRequest(userId, sessionId, itemCode, "RG", timeUnit, ssDateFrom, ssDateTo)

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

    fun getStockPos(userId: String, accno: String, sessionId: String, stockCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val stockPosRequest = StockPosRequest(userId, accno, sessionId, stockCode)

            getStockPosUseCase.invoke(stockPosRequest).collect() { res ->
                res.let {
                    withContext(Dispatchers.Main){
                        getStockPosResult.postValue(it)
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

    fun getSessionPin(userId: String){
        CoroutineScope(Dispatchers.IO).launch {
            getSessionPin.invoke(userId).collect() {resource ->
                when (resource){
                    is Resource.Success -> {
                        getSessionPinResult.postValue(resource.data)
                    }

                    else -> {}
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

    fun getStockInfoDetail(userId: String, sessionId: String, stockCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val stockInfoDetailRequest = StockInfoDetailRequest(userId, sessionId, stockCode)

            getStockInfoDetailUseCase.invoke(stockInfoDetailRequest).collect() { res ->
                res.let {
                    withContext(Dispatchers.Main){
                        getStockInfoDetailResult.postValue(it)
                    }
                }
            }
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

    fun isPriceAlertEmpty(userId: String, sessionId: String, stockCode: String) {

        var isEmpty = true

        viewModelScope.launch {
            val priceAlertReq = PriceAlertReq(userId, sessionId, stockCode)

            getListPriceAlertUseCase.invoke(priceAlertReq).collect() {resource ->
                resource.let {
                    when (resource) {
                        is Resource.Success -> {
                            isEmpty = if (resource.data != null) {
                                when (resource.data.status) {
                                    0 -> {
                                        val listData = resource.data.priceAlertList.filter { it.triggerAt == 0L }
                                        listData.isEmpty()
                                    }

                                    else -> true
                                }
                            } else {
                                true
                            }


                        }
                        else -> {}
                    }
                    isPriceAlertEmpty.postValue(isEmpty)
                }
            }
        }
    }

    fun clearisPriceAlertEmpty(){
        isPriceAlertEmpty.postValue(null)
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

    fun startRealtimeDataPortofolio(accNo: String) {
        viewModelScope.launch {
            setListenerCifStockPos() // Launches the first function
            delay(200)
            subsCifStockPos(accNo) // Waits for the second to complete, then executes
        }
    }

    fun stopRealtimeDataPortofolio(accNo: String) {
        viewModelScope.launch {
            unSubsCifStockPos(accNo)
        }
    }

    private fun setListenerCifStockPos() {
        viewModelScope.launch(Dispatchers.IO) {
            setListenerCIFStockPosUseCase.setListenerCIFStockPos(cakraListener)
//            Timber.tag("accPosRealtime").d("start-------")
        }
    }

    fun publishAccPos(userId: String, sessionId: String, subsOp: Int, accNo: String){
        viewModelScope.launch(Dispatchers.IO) {
            val publishAccPosReq = PublishAccPosReq(userId, sessionId, subsOp, accNo)

            publishAccPosUseCase.publishFastOrderRepo(publishAccPosReq)
//            Log.d("accPosRealtime", "simple portfolio publish: $subsOp")
        }

    }

    private fun subsCifStockPos(accNo: String){
        viewModelScope.launch(Dispatchers.IO){
            subscribeCIFStockPosUseCase.subscribeCIFStockPos(accNo)
//            Log.d("accPosRealtime", "simple portfolio subscribe")
        }
    }

    private fun unSubsCifStockPos(accNo: String){
        viewModelScope.launch(Dispatchers.IO){
            unsubscribeCIFStockPosUseCase.unsubscribeCIFStockPos(accNo)
//            Log.d("accPosRealtime", "simple portfolio unsubscribe")
        }
    }

    private val cakraListener = MQMessageListener<CakraMessage> { result ->
        viewModelScope.launch(Dispatchers.IO) {
            val parsedObject = result.protoMsg
            if (parsedObject.type == CakraMessage.Type.SIMPLE_PORTOFOLIO_RESPONSE) {
                val listStockPos = parsedObject.simplePortofolioResponse.accStockPosList
                if (listStockPos.isNotEmpty()) {
                    listStockPos.forEach { item ->
                        if (item.stockcode == stockCode) {
                            val marketValue = item.realStockAvailable * item.reffprice
                            val profitLoss = (item.reffprice - item.avgprice) * item.realStockAvailable
                            val value = item.avgprice * item.realStockAvailable
                            val gainloss = marketValue - value
                            val pct = (gainloss / value) * 100
                            val qty = item.realStockAvailable / 100
                            val haircut = 100.minus(item.pctStockVal)

                            val data = PortfolioStockDataItem(
                                item.stockcode,
                                item.reffprice,
                                item.avgprice,
                                marketValue,
                                pct,
                                profitLoss,
                                qty,
                                haircut,
                                potentialLot = item.potStockAvailable / 100,
                                totalAsset = item.totalAsset
                            )

                            withContext(Dispatchers.Main) {
                                getRealtimeStockPos.postValue(data)
                            }
                        }
                    }
                }
            }


//            if (event.protoMsg.groupCashPosList.isNotEmpty()) {
//                getSummaryResult.postValue(event.protoMsg.groupCashPosList)
//            }
        }
    }
}