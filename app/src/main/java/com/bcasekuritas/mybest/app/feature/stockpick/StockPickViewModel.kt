package com.bcasekuritas.mybest.app.feature.stockpick

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.StockPickReportReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockPickRequest
import com.bcasekuritas.mybest.app.domain.interactors.GetStockPickReportUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockPickUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.common.getCurrentDate
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.proto.news.NewsStockPickSingleResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewStockPickResearchReport
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockPickViewModel @Inject constructor(
    private val getStockPickUseCase: GetStockPickUseCase,
    private val getStockPickReportUseCase: GetStockPickReportUseCase,
    val imqConnectionListener: IMQConnectionListener
): BaseViewModel() {

    val getStockPickResult = MutableLiveData<NewsStockPickSingleResponse?>()
    val getStockPickReportResult = MutableLiveData<ViewStockPickResearchReport?>()

    @RequiresApi(Build.VERSION_CODES.O)
    fun getStockPick(userId: String, sessionId: String, status: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val stockPickReq = StockPickRequest(userId, sessionId, status)

            getStockPickUseCase.invoke(stockPickReq).collect(){resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            if (it.data != null) {
                                getStockPickResult.postValue(it.data)
                            }
                        } else -> {}
                    }
                }
            }
        }
    }

    fun getStockPickReport(userId: String, sessionId: String){
        viewModelScope.launch(Dispatchers.IO) {
            val stockPickReq = StockPickReportReq(userId, sessionId)

            getStockPickReportUseCase.invoke(stockPickReq).collect(){resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            if (it.data?.stockPickResearchReportData != null) {
                                getStockPickReportResult.postValue(it.data.stockPickResearchReportData)
                            }
                        } else -> {}
                    }
                }
            }
        }
    }

}