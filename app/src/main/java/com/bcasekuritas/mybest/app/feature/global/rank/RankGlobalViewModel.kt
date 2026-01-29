package com.bcasekuritas.mybest.app.feature.global.rank

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.GlobalRankReq
import com.bcasekuritas.mybest.app.domain.interactors.GetGlobalRankUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.PagingManager
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankGlobalActivityDiscover
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankGlobalViewModel @Inject constructor(
    private val getGlobalRankUseCase: GetGlobalRankUseCase
) : BaseViewModel() {
    private val pagingManager = PagingManager<BrokerRankGlobalActivityDiscover>()
    private var currentPage = 0
    private val pageSize = 20

    val getGlobalRankResult = MutableLiveData<List<BrokerRankGlobalActivityDiscover>>()

    fun getGlobalRank(userId: String, sessionId: String, sortField: Int,board:String, activity: Int, startDate: Long, endDate: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val globalRankRequest = GlobalRankReq(
                userId,
                sessionId,
                sortField,
                board,
                activity,
                startDate,
                endDate
            )

            getGlobalRankUseCase.invoke(globalRankRequest).collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        currentPage = 0
                        pagingManager.clearData()
                        if (resource.data != null) {
                            pagingManager.addData(resource.data.brokerRankActivityDiscoverList)
                            loadPage(0)
                        }
                    }
                    else -> {}
                }
            }
        }
    }


    private fun loadPage(page: Int) {
        val pageData = pagingManager.getPage(page, pageSize)
        getGlobalRankResult.postValue(pageData)
    }

    fun loadNextPage() {
        currentPage++
        if (pagingManager.hasMoreData(currentPage, pageSize)) {
            loadPage(currentPage)
        }
    }
}