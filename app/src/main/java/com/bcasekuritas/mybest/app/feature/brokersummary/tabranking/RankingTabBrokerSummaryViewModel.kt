package com.bcasekuritas.mybest.app.feature.brokersummary.tabranking

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerRankByStockReq
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerSummaryRankingReq
import com.bcasekuritas.mybest.app.domain.interactors.GetBrokerRankRankingUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.PagingManager
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockDiscover
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankingDiscover
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankingTabBrokerSummaryViewModel @Inject constructor(
    private val getBrokerRankRankingUseCase: GetBrokerRankRankingUseCase
): BaseViewModel() {
    private val pagingManager = PagingManager<BrokerRankingDiscover>()
    private var currentPage = 0
    private val pageSize = 20
    private var brokerrankfilter = BrokerSummaryRankingReq()

    val getDataPage = MutableLiveData<Int>()
    val getBrokerSummaryRankingResult = MutableLiveData<List<BrokerRankingDiscover>>()

    fun setFilter(brokerRankReq: BrokerSummaryRankingReq) {
        brokerrankfilter = brokerRankReq
    }

    fun getBrokerSummaryRanking(userId: String, sessionId: String, startDate: Long, endDate: Long, sortType: Int) {
        viewModelScope.launch(Dispatchers.IO){
            val brokerSumRequest = BrokerSummaryRankingReq(userId, startDate, endDate, sessionId, sortType)
            setFilter(brokerSumRequest)

            getBrokerRankRankingUseCase.invoke(brokerSumRequest).collect() {resource ->
                when(resource) {
                    is Resource.Success -> {
                        if (resource.data != null) {
                            if (brokerSumRequest == brokerrankfilter) {
                                currentPage = 0
                                pagingManager.clearData()
                                pagingManager.addData(resource.data.brokerRankingDiscoverList)
                                loadPage(0)
                            }
                        }

                    }
                    else -> {}
                }
            }
        }
    }

    private fun loadPage(page: Int) {
        getDataPage.postValue(page)
        val pageData = pagingManager.getPage(page, pageSize)
        getBrokerSummaryRankingResult.postValue(pageData)
    }

    fun loadNextPage() {
        currentPage++
        if (pagingManager.hasMoreData(currentPage, pageSize)) {
            loadPage(currentPage)
        }
    }

}