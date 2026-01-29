package com.bcasekuritas.mybest.app.feature.brokersummary.tabbystock

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerRankByStockReq
import com.bcasekuritas.mybest.app.domain.dto.response.BrokerSummaryByStock
import com.bcasekuritas.mybest.app.domain.interactors.GetBrokerRankByStockUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetBrokerSummaryByStockNetUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SearchStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.PagingManager
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockDiscover
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ByStockTabBrokerSummaryViewModel @Inject constructor(
    private val searchStockParamDaoUseCase: SearchStockParamDaoUseCase,
    private val brokerRankByStockUseCase: GetBrokerRankByStockUseCase,
    private val brokerSummaryByStockNetUseCase: GetBrokerSummaryByStockNetUseCase
): BaseViewModel() {
    private val pagingManager = PagingManager<BrokerSummaryByStock>()
    private var currentPage = 0
    private val pageSize = 20
    private var brokerrankfilter = BrokerRankByStockReq()

    fun setFilter(brokerRankByStockReq: BrokerRankByStockReq) {
        brokerrankfilter = brokerRankByStockReq
    }

    val getBrokerSumResult = MutableLiveData<List<BrokerSummaryByStock>>()
    var getAllStockParamResult = MutableLiveData<List<StockParamObject?>>()
    val getDataPage = MutableLiveData<Int>()

    fun getBrokerStockSum(brokerRankByStockReq: BrokerRankByStockReq) {
        viewModelScope.launch(Dispatchers.IO) {
            setFilter(brokerRankByStockReq)

            brokerRankByStockUseCase.invoke(brokerRankByStockReq).collect(){resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            if (it.data != null) {
                                when (it.data.status){
                                    0 -> {
                                        if (brokerRankByStockReq == brokerrankfilter) {
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

    fun getBrokerStockNet(brokerRankByStockReq: BrokerRankByStockReq) {
        viewModelScope.launch(Dispatchers.IO) {
            setFilter(brokerRankByStockReq)
            brokerSummaryByStockNetUseCase.invoke(brokerRankByStockReq).collect(){resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            if (it.data != null) {
                                when (it.data.status){
                                    0 -> {
                                        if (brokerRankByStockReq == brokerrankfilter) {
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
                            }
                            Timber.d("broker : $it")
                        }
                        else -> {}
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

    private fun loadPage(page: Int) {
        getDataPage.postValue(page)
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