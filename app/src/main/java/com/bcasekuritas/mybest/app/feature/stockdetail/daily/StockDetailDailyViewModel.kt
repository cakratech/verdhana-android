package com.bcasekuritas.mybest.app.feature.stockdetail.daily

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.TradeSumRequest
import com.bcasekuritas.mybest.app.domain.dto.response.BrokerSummaryByStock
import com.bcasekuritas.mybest.app.domain.interactors.GetTradeSummaryUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.PagingManager
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityDiscover
import com.bcasekuritas.rabbitmq.proto.chart.Cf
import com.bcasekuritas.rabbitmq.proto.chart.Cf.CFMessage.TradeSummaryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StockDetailDailyViewModel @Inject constructor(
    private val getTradeSummaryUseCase: GetTradeSummaryUseCase
): BaseViewModel(){
    private val pagingManager = PagingManager<Cf. CFMessage. TradeSummary>()
    private var currentPage = 0
    private val pageSize = 20

    val getDataPage = MutableLiveData<Int>()

    val getTradeSummaryResult = MutableLiveData<List<Cf.CFMessage.TradeSummary>>()
    val hideLoading = MutableLiveData<Boolean>()

    fun getTradeSummary(userId: String, sessionId: String, stockCode: String, startDate: Long, endDate: Long){
        viewModelScope.launch(Dispatchers.IO) {
            val tradeSummaRequeste = TradeSumRequest(userId, sessionId, stockCode, startDate, endDate)

            getTradeSummaryUseCase.invoke(tradeSummaRequeste).collect() { resource ->
                when (resource) {
                    is Resource.Success -> {
                        hideLoading.postValue(true)
                        if (resource.data != null) {
                            Timber.d("Total daily data: ${resource.data.tradeSummaryCount}")
                            currentPage = 0
                            pagingManager.clearData()
                            pagingManager.addData(
                                resource.data.tradeSummaryList
                                    .filterNotNull()
                                    .sortedByDescending { it.tradeDate }
                            )
                            loadPage(0)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun loadPage(page: Int) {
        getDataPage.postValue(page)
        val pageData = pagingManager.getPage(page, pageSize).toList()
        getTradeSummaryResult.postValue(pageData)
    }

    fun loadNextPage() {
        currentPage++
        if (pagingManager.hasMoreData(currentPage, pageSize)) {
            loadPage(currentPage)
        }
    }
}