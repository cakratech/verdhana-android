package com.bcasekuritas.mybest.app.feature.portfolio.tabportfolio

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.LogoutReq
import com.bcasekuritas.mybest.app.domain.dto.request.PublishAccPosReq
import com.bcasekuritas.mybest.app.domain.dto.request.SessionRequest
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioStockDataItem
import com.bcasekuritas.mybest.app.domain.interactors.DeleteSessionsDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetListStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetLogoutUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSimplePortfolioUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockPosUseCase
import com.bcasekuritas.mybest.app.domain.interactors.PublishAccPosUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SetListenerCIFStockPosUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SubscribeCIFStockPosUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UnsubscribeCIFStockPosUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.bcas.AccStockPosResponse
import com.bcasekuritas.rabbitmq.proto.bcas.CakraMessage
import com.bcasekuritas.rabbitmq.proto.bcas.GroupCashPos
import com.bcasekuritas.rabbitmq.proto.bcas.LogoutResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleAccBondPos
import com.bcasekuritas.rabbitmq.proto.bcas.SimplePortofolio
import com.bcasekuritas.rabbitmq.proto.bcas.SimplePortofolioResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PortfolioTabViewModel @Inject constructor(
    private val getStockPosUseCase: GetStockPosUseCase,
    private val getSimplePortfolioUseCase: GetSimplePortfolioUseCase,
    private val getListStockParamDaoUseCase: GetListStockParamDaoUseCase,
    private val publishAccPosUseCase: PublishAccPosUseCase,
    private val logoutUseCase: GetLogoutUseCase,
    private val setListenerCIFStockPosUseCase: SetListenerCIFStockPosUseCase,
    private val subscribeCIFStockPosUseCase: SubscribeCIFStockPosUseCase,
    private val unsubscribeCIFStockPosUseCase: UnsubscribeCIFStockPosUseCase,
    private val deleteSessions: DeleteSessionsDaoUseCase,
    val imqConnectionListener: IMQConnectionListener
): BaseViewModel() {

    val getStockPosResult = MutableLiveData<Resource<AccStockPosResponse?>>()
    val getSimplePortfolioResult = MutableLiveData<Resource<SimplePortofolioResponse?>>()
    val getSimplePortfolioRealtimeResult = MutableLiveData<SimplePortofolio>()
    val getListPortfolio = MutableLiveData<List<PortfolioStockDataItem>>()
    val getSummaryResult = MutableLiveData<List<GroupCashPos>>()
    val isEmptyPortfolio = MutableLiveData<Boolean>()
    val getLayoutState = MutableLiveData<Int>()
    val getListBondsResult = MutableLiveData<List<SimpleAccBondPos>>()
    val getLogoutResult = MutableLiveData<LogoutResponse?>()

    private val stockDataMapper = mutableMapOf<String, PortfolioStockDataItem>()

    fun startRealtimeData(userId: String, sessionId: String, accNo: String) {
        viewModelScope.launch {
            setListenerCifStockPos() // Launches the first function
            delay(200)
            subsCifStockPos(accNo) // Waits for the second to complete, then executes
        }
    }


    fun stopRealtimeData(userId: String, sessionId: String, accNo: String) {
        viewModelScope.launch {
            unSubsCifStockPos(accNo)
        }
    }

    private fun setListenerCifStockPos() {
        viewModelScope.launch(Dispatchers.IO) {
            setListenerCIFStockPosUseCase.setListenerCIFStockPos(cakraListener)
//            Timber.tag("accPosRealtime").d( "start-------")
        }
    }

    fun publishAccPos(userId: String, sessionId: String, subsOp: Int, accNo: String){
        viewModelScope.launch(Dispatchers.IO) {
            val publishAccPosReq = PublishAccPosReq(userId, sessionId, subsOp, accNo)

            publishAccPosUseCase.publishFastOrderRepo(publishAccPosReq)
//            Timber.tag("accPosRealtime").d( "publish $accNo")
        }

    }

    private fun subsCifStockPos(accNo: String){
        viewModelScope.launch(Dispatchers.IO){
            subscribeCIFStockPosUseCase.subscribeCIFStockPos(accNo)
//            Timber.tag("accPosRealtime").d( "subscribe $accNo")
        }
    }

    private fun unSubsCifStockPos(accNo: String){
        viewModelScope.launch(Dispatchers.IO){
            unsubscribeCIFStockPosUseCase.unsubscribeCIFStockPos(accNo)
//            Timber.tag("accPosRealtime").d( "unsubscribe $accNo")
        }
    }

    fun getSimplePortfolio(userId: String, sessionId: String, accNo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val simplePortfolio = SessionRequest(userId, sessionId, accNo)

            getSimplePortfolioUseCase.invoke(simplePortfolio).collect() {resource ->
                resource.let {
                    withContext(Dispatchers.Main) {
                        getSimplePortfolioResult.postValue(it)
                        try {
                            when(it){
                                is Resource.Success -> {
                                    stockDataMapper.clear()
                                    val data = it.data?.accStockPosList?.filter{ it.realStockAvailable > 0.0 }
                                    if (data?.size != 0) {
                                        data?.map {item ->
                                            val marketValue = item.realStockAvailable * item.reffprice
                                            val profitLoss = (item.reffprice - item.avgprice) * item.realStockAvailable
                                            val value = item.avgprice * item.realStockAvailable
                                            val gainloss = marketValue - value
                                            val pct = (gainloss / value) * 100
                                            val qty = item.realStockAvailable / 100
                                            val haircut = 100.minus(item.pctStockVal)

                                            stockDataMapper[item.stockcode] = PortfolioStockDataItem(
                                                item.stockcode,
                                                item.reffprice,
                                                item.avgprice,
                                                marketValue,
                                                pct,
                                                profitLoss,
                                                qty,
                                                haircut,
                                                potentialLot = item.potStockAvailable / 100,
                                                totalAsset = item.totalAsset,
                                                blockedLot = item.blockQty
                                            )
                                        }
                                    }
                                    val listBonds = it.data?.simpleAccBondPosList
                                    val layoutState = when {
                                        // 0: stock only, 1: bonds only, 2: both
                                        listBonds?.isNotEmpty() == true && data?.isNotEmpty() == true -> 2
                                        data?.isNotEmpty() == true && listBonds.isNullOrEmpty() -> 0
                                        listBonds?.isNotEmpty() == true && data.isNullOrEmpty() -> 1
                                        else -> 0
                                    }
                                    getLayoutState.postValue(layoutState)
                                    getListBondsResult.postValue(listBonds?.filterNotNull())
                                    getListStockParam(stockDataMapper.keys.toList())
                                }

                                else -> {}
                            }
                        } catch (e: Exception){
                            Timber.tag("PortfolioTabView").d("Potentially Out Of Bound")
                        }
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
                            data.map {item ->
                                val listNotation = item?.stockNotation?.map { it.notation }
                                stockDataMapper[item?.stockParam?.stockCode]?.let {
                                    if (item != null) {
                                        if (listNotation != null) {
                                            it.notation = listNotation.joinToString()
                                        }
                                        it.idxBoard = item.stockParam.idxTrdBoard
                                    }
                                }
                            }
                            withContext(Dispatchers.Main) {
                                getListPortfolio.postValue(ArrayList(stockDataMapper.values))
                            }
//                            withContext(Dispatchers.Main) {
//                                getStockParamResult.postValue(data)
//                            }
                        }
                    }

                    else -> {
                        // Handle other cases if needed
                    }
                }
            }
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

    private val cakraListener = MQMessageListener<CakraMessage> { result ->
        viewModelScope.launch(Dispatchers.IO) {
            Timber.tag("accPosRealtime").d( "get realtime data")
            try {

                val parsedObject = result.protoMsg
                if (parsedObject.type == CakraMessage.Type.SIMPLE_PORTOFOLIO_RESPONSE) {
                    val portfolio = parsedObject.simplePortofolioResponse.simplePortofolio
                    withContext(Dispatchers.Main) {
                        getSimplePortfolioRealtimeResult.postValue(portfolio)
                    }
                    //
                    val listBonds = parsedObject.simplePortofolioResponse.simpleAccBondPosList
                    withContext(Dispatchers.Main) {
                        getListBondsResult.postValue(listBonds)
                    }
                    //
                    val listStockPos = parsedObject.simplePortofolioResponse.accStockPosList.filter { it.realStockAvailable > 0.0 }
                    if (listStockPos.isNotEmpty()) {
                        listStockPos.map { item ->
                            val qty = item.realStockAvailable
                            val marketValue = qty * item.reffprice
                            val profitLoss = (item.reffprice - item.avgprice) * qty
                            val value = item.avgprice * qty
                            val gainloss = marketValue - value
                            val pct = (gainloss / value) * 100
                            val lot = qty / 100
                            val haircut = 100.minus(item.pctStockVal)

                            stockDataMapper[item.stockcode] = PortfolioStockDataItem(
                                item.stockcode,
                                item.reffprice,
                                item.avgprice,
                                marketValue,
                                pct,
                                profitLoss,
                                lot,
                                haircut,
                                potentialLot = item.potStockAvailable / 100,
                                totalAsset = item.totalAsset,
                                blockedLot = item.blockQty
                            )
                        }

                        getListStockParam(stockDataMapper.keys.toList())
                    }
                }
            } catch (e: Exception){
                Timber.tag("PortfolioTabViewModel").d("Potentially Out Of Bound")
            }
        }
    }
}