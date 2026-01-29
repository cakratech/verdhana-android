package com.bcasekuritas.mybest.app.feature.portfolio.realized

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.domain.dto.request.RealizedGainLossMonthRequest
import com.bcasekuritas.mybest.app.domain.dto.request.RealizedGainLossYearRequest
import com.bcasekuritas.mybest.app.domain.dto.response.RealizedGainLossRes
import com.bcasekuritas.mybest.app.domain.interactors.GetRealizedGainLossByMonthUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetRealizedGainLossByYearUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SearchStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.rabbitmq.proto.bcas.RGainLossResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RealizeGainLossResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RealizedViewModel @Inject constructor(
    private val searchStockParamDaoUseCase: SearchStockParamDaoUseCase,
    private val getRealizedGainLossByYearUseCase: GetRealizedGainLossByYearUseCase,
    private val getRealizedGainLossByMonthUseCase: GetRealizedGainLossByMonthUseCase
): BaseViewModel() {

    var getAllStockParamResult = MutableLiveData<List<StockParamObject?>>()
    var getRealizedGainLossByYear = MutableLiveData<RGainLossResponse?>()
    var getRealizedGainLossListStock = MutableLiveData<List<RealizedGainLossRes?>>()

    fun getRealizedByYear(userId: String, accNo: String, sessionId: String, year: Int, stockCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = RealizedGainLossYearRequest(userId, accNo, sessionId, year, stockCode)

            getRealizedGainLossByYearUseCase.invoke(request).collect() { resource ->
                resource.let {
                    when (resource) {
                        is Resource.Success -> {
                            val data = resource.data
                            getRealizedGainLossByYear.postValue(data)

                        }
                        else -> {}
                    }
                }

            }
        }
    }

    fun getRealizedStock(userId: String, accNo: String, sessionId: String, year: Int, month: Int, stockCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = RealizedGainLossMonthRequest(userId, accNo, sessionId, year, month, stockCode)

            getRealizedGainLossByMonthUseCase.invoke(request).collect() { resource ->
                resource.let {
                    when (resource) {
                        is Resource.Success -> {
                            val data = resource.data?.realizeGainLossList
                            val listItems = arrayListOf<RealizedGainLossRes>()
                            var lastDate: String? = null
                            if (!data.isNullOrEmpty()) {
                                data.sortedBy { it.date }.forEach { item ->
                                    val date = DateUtils.convertLongToDate(item.date, "dd MMM yyyy")
                                    if (date != lastDate) {
                                        listItems.add(RealizedGainLossRes(date = item.date, isDateDivider = true))
                                        lastDate = date
                                    }
                                    listItems.add(
                                        RealizedGainLossRes(
                                            stockCode = item.stockCode,
                                            date = item.date,
                                            year = item.year,
                                            month = item.month,
                                            profitLoss = item.profitLoss,
                                            profitLossPct = item.profitLossPct,
                                            isDateDivider = false
                                        )
                                    )
                                }
                                getRealizedGainLossListStock.postValue(listItems)
                            }

                        }
                        else -> {}
                    }
                }

            }
        }
    }

    fun getAllStockParam() {
        viewModelScope.launch {
            val resultList = mutableListOf<StockParamObject?>()
            searchStockParamDaoUseCase("").collect { resource ->
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


}