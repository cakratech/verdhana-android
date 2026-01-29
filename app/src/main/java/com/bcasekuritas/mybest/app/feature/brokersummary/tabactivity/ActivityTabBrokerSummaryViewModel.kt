package com.bcasekuritas.mybest.app.feature.brokersummary.tabactivity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerRankActivityReq
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerRankByStockReq
import com.bcasekuritas.mybest.app.domain.interactors.GetBrokerListUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetBrokerRankActivityUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.PagingManager
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerListInfo
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityDiscover
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockDiscover
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityTabBrokerSummaryViewModel @Inject constructor(
    private val getBrokerListUseCase: GetBrokerListUseCase,
    private val getBrokerRankActivityUseCase: GetBrokerRankActivityUseCase
): BaseViewModel() {
    private val pagingManager = PagingManager<BrokerRankActivityDiscover>()
    private var currentPage = 0
    private val pageSize = 20
    private var brokerrankfilter = BrokerRankActivityReq()

    val getDataPage = MutableLiveData<Int>()

    fun setFilter(brokerRankFilter: BrokerRankActivityReq) {
        brokerrankfilter = brokerRankFilter
    }

    val getBrokerListResult = MutableLiveData<List<String>>()
    val getBrokerRankActivityResult = MutableLiveData<List<BrokerRankActivityDiscover>>()

    fun getBrokerList(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getBrokerListUseCase.invoke(userId).collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        val brokerList = arrayListOf<String>()
                        resource.data?.brokerListInfoList?.map { brokerList.add(it.initcode) }
                        getBrokerListResult.postValue(brokerList)
                    }
                    else -> {}
                }
            }
        }
    }

    fun getBrokerRankActivity(userId: String, sessionId: String, brokerCode: String, boardCode: String, startDate: Long, endDate: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = BrokerRankActivityReq(userId, startDate, endDate, boardCode, brokerCode, sessionId)
            setFilter(request)

            getBrokerRankActivityUseCase.invoke(request).collect() { resource ->
                when (resource) {
                    is Resource.Success -> {
                        if (resource.data != null) {
                            if (request == brokerrankfilter) {
                                currentPage = 0
                                pagingManager.clearData()
                                pagingManager.addData(resource.data.brokerRankActivityDiscoverList.filterNotNull())
                                loadPage(0)
                            }
                        }

                    }
                    else -> {}
                }
            }
        }
    }

    fun loadPage(page: Int) {
        getDataPage.postValue(page)
        val pageData = pagingManager.getPage(page, pageSize)
        getBrokerRankActivityResult.postValue(pageData)
    }

    fun loadNextPage() {
        currentPage++
        if (pagingManager.hasMoreData(currentPage, pageSize)) {
            loadPage(currentPage)
        }
    }

}