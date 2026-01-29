package com.bcasekuritas.mybest.app.feature.stockdetail.brokersummary

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerRankByStockReq
import com.bcasekuritas.mybest.app.domain.dto.response.BrokerSummaryByStock
import com.bcasekuritas.mybest.app.domain.interactors.GetBrokerRankByStockUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetBrokerSummaryByStockNetUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.PagingManager
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockDiscover
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockDiscoverResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StockDetailBrokerSummaryViewModel @Inject constructor(
    private val brokerRankByStockUseCase: GetBrokerRankByStockUseCase,
    private val brokerSummaryByStockNetUseCase: GetBrokerSummaryByStockNetUseCase
): BaseViewModel() {
    private val pagingManager = PagingManager<BrokerSummaryByStock>()
    private var currentPage = 0
    private val pageSize = 20

    val getBrokerSumResult = MutableLiveData<List<BrokerSummaryByStock>>()

    fun getBrokerStockSum(brokerRankByStockReq: BrokerRankByStockReq) {
        viewModelScope.launch(Dispatchers.IO) {

            brokerRankByStockUseCase.invoke(brokerRankByStockReq).collect(){ resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            if (it.data != null) {
                                when (it.data.status) {
                                    0 -> {
                                        val listData = it.data.brokerRankByStockDiscoverList.map { item ->
                                            BrokerSummaryByStock(
                                                item.brokerCodeBuy,
                                                item.brokerCodeSell,
                                                item.buyLot,
                                                item.buyVal,
                                                item.buyAvg,
                                                item.sellLot,
                                                item.sellVal,
                                                item.sellAvg
                                            )
                                        }
                                        currentPage = 0
                                        pagingManager.clearData()
                                        pagingManager.addData(listData)
                                        loadPage(0)

                                    }
                                    else -> {
                                        getBrokerSumResult.postValue(emptyList())
                                    }
                                }
                            } else {
                                getBrokerSumResult.postValue(emptyList())
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun getBrokerStockNet(brokerRankByStockReq: BrokerRankByStockReq) {
        viewModelScope.launch(Dispatchers.IO) {

            brokerSummaryByStockNetUseCase.invoke(brokerRankByStockReq).collect(){resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            if (it.data != null) {
                                when (it.data.status){
                                    0 -> {
                                        val listData = it.data.brokerRankByStockNetDiscoverList.map { item ->
                                            BrokerSummaryByStock(
                                                item.brokerbuy,
                                                item.brokersell,
                                                item.netlotbuy,
                                                item.netvalbuy,
                                                item.avgbuy3,
                                                item.netlotsell,
                                                item.netvalsell,
                                                item.avgSell3
                                            )
                                        }
                                        currentPage = 0
                                        pagingManager.clearData()
                                        pagingManager.addData(listData)
                                        loadPage(0)
                                    }
                                }
                            }
                            Timber.d("broker : $it")
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun loadPage(page: Int) {
        val pageData = pagingManager.getPage(page, pageSize)
        getBrokerSumResult.postValue(pageData)
    }

    fun loadNextPage() {
        currentPage++
        if (pagingManager.hasMoreData(currentPage, pageSize)) {
            loadPage(currentPage)
        }
    }
}