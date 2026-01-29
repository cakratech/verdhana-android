package com.bcasekuritas.mybest.app.feature.dialog.bottom.runningtrade

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.FilterRunningTradeObject
import com.bcasekuritas.mybest.app.data.layout.UIDialogRunningTradeModel
import com.bcasekuritas.mybest.app.domain.dto.request.AllUserWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IndexSectorRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockIndexSectorRequest
import com.bcasekuritas.mybest.app.domain.dto.request.UserWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.response.IndexSectorDetailData
import com.bcasekuritas.mybest.app.domain.dto.response.TradeSummary
import com.bcasekuritas.mybest.app.domain.interactors.GetFilterRunningTradeUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetIndexSectorUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSimpleAllWatchlistUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetSimpleWatchlistUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockIndexSectorUseCase
import com.bcasekuritas.mybest.app.domain.interactors.ResetDefaultFilterRunningTradeUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SetDefaultFilterRunningTradeUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.converter.GET_IMAGE_SECTOR
import com.bcasekuritas.mybest.ext.event.SingleLiveEvent
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleAllUserWatchListResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewIndexSector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DialogFilterRunningTradeViewModel @Inject constructor(
    private val getStockIndexUseCase: GetStockIndexSectorUseCase,
    private val getIndexSectorUseCase: GetIndexSectorUseCase,
    private val getSimpleAllWatchlistUseCase: GetSimpleAllWatchlistUseCase,
    private val getSimpleWatchlistUseCase: GetSimpleWatchlistUseCase,
    private val insertDefaultFilterUseCase: SetDefaultFilterRunningTradeUseCase,
    private val resetDefaultFilterRunningTradeUseCase: ResetDefaultFilterRunningTradeUseCase
    ): BaseViewModel() {
    val getListIndex = MutableLiveData<List<ViewIndexSector>>()
    val getListSector = MutableLiveData<List<ViewIndexSector>>()
    val getListStockIndexSector = SingleLiveEvent<List<String>>()

    fun getIndexSectorData(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = IndexSectorRequest(userId, sessionId, 0)

            getIndexSectorUseCase.invoke(request).collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        val listAllIndex = resource.data?.filter { it?.sector == false }?.sortedBy { it?.indexCode }
                        val listAllSector = resource.data?.filter { it?.sector == true }?.sortedBy { it?.indexCode }
                        getListIndex.postValue(listAllIndex?.filterNotNull())
                        getListSector.postValue(listAllSector?.filterNotNull())

                    }
                    else -> {}
                }

            }


        }
    }

    fun getStockIndexSector(userId: String, sessionId: String, indexId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val stockIndexReq = StockIndexSectorRequest(userId, sessionId, indexId, 0, 0, false)

            getStockIndexUseCase.invoke(stockIndexReq).collect() {resource ->
                when(resource) {
                    is Resource.Success -> {
                        if (resource.data != null) {
                            if (resource.data.dataList.size != 0) {
                                val listStock = resource.data.dataList.map { it.stockCode }
                                Log.d("filterr", "stocks index/sector ${listStock.size}")
                                getListStockIndexSector.postValue(listStock)
                            }
                        }

                    }
                    else -> {}
                }
            }
        }

    }

    fun getAllWatchlist(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val watchlistRequest = AllUserWatchListRequest(userId, sessionId)

            getSimpleAllWatchlistUseCase.invoke(watchlistRequest).collect() { resource ->
                when (resource) {
                    is Resource.Success -> {
                        if (resource.data != null) {
                            val data = resource.data.userWatchListItemList
                            if (data.isNotEmpty()) {
                                val listStock = data.map { it.itemCode }
                                Log.d("filterr", "stocks watchlist ${listStock.size}")
                                getListStockIndexSector.postValue(listStock)
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun getListStockPortfolio(userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val watchListRequest =
                UserWatchListRequest(
                    userId,
                    sessionId = sessionId,
                    "Portfolio"
                ) //if wlg empty = get all watchlist

            getSimpleWatchlistUseCase.invoke(watchListRequest).collect() { resource ->
                when (resource) {
                    is Resource.Success -> {
                        if (resource.data != null) {
                            val data = resource.data.userWatchListList
                            if (data.isNotEmpty()) {
                                val listStock = data[0].userWatchListItemList.map { it.itemCode }
                                Log.d("filterr", "stocks portfolio ${listStock.size}")

                                getListStockIndexSector.postValue(listStock)
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun insertDefaultFilter(userId: String, filter: UIDialogRunningTradeModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val filterObject = FilterRunningTradeObject(
                userId,
                filter.indexSector,
                filter.category,
                filter.priceFrom,
                filter.priceTo,
                filter.changeFrom,
                filter.changeTo,
                filter.volumeFrom,
                filter.volumeTo,
                filter.stockCodes
            )

            insertDefaultFilterUseCase.setDefaultFilter(filterObject)
        }
    }

    fun resetDefaultFilterRunningTrade(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            resetDefaultFilterRunningTradeUseCase.resetDefaultFilter(userId)
        }
    }


}