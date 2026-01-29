package com.bcasekuritas.mybest.app.feature.stockdetail.analysis

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.FibonacciPivotPointReq
import com.bcasekuritas.mybest.app.domain.dto.request.GetPbvBandReq
import com.bcasekuritas.mybest.app.domain.dto.request.GetPerBandReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockAnalysisRatingReq
import com.bcasekuritas.mybest.app.domain.interactors.GetFibonacciPivotPointUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetPbvBandUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetPbvDataUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetPerBandUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetPerDataUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockAnalysisRatingUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.news.FibonacciPivotPointResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPbvBandResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPbvDataResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPerBandResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPerDataResponse
import com.bcasekuritas.rabbitmq.proto.news.StockAnalysisRatingResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockDetailAnalysisViewModel @Inject constructor(
    private val getPerBandUseCase: GetPerBandUseCase,
    private val getPerDataUseCase: GetPerDataUseCase,
    private val getPbvBandUseCase: GetPbvBandUseCase,
    private val getPbvDataUseCase: GetPbvDataUseCase,
    private val stockAnalysisRatingUseCase: GetStockAnalysisRatingUseCase,
    private val fibonacciPivotPointUseCase: GetFibonacciPivotPointUseCase
): BaseViewModel(){
    val getPerBandResult = MutableLiveData<Resource<GetPerBandResponse?>>()
    val getPerDataResult = MutableLiveData<Resource<GetPerDataResponse?>>()
    val getPbvBandResult = MutableLiveData<Resource<GetPbvBandResponse?>>()
    val getPbvDataResult = MutableLiveData<Resource<GetPbvDataResponse?>>()
    val getStockAnalysisRatingResult = MutableLiveData<Resource<StockAnalysisRatingResponse?>>()
    val getFibonacciPivotPointResult = MutableLiveData<Resource<FibonacciPivotPointResponse?>>()

    fun getPerBand(userId: String, userSession: String, stockCode: String, period: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val getPerBandReq = GetPerBandReq(userId,userSession, stockCode, period)

            getPerBandUseCase.invoke(getPerBandReq).collect(){resource ->
                resource.let {
                    getPerBandResult.postValue(it)
                }
            }
        }
    }

    fun getPerData(userId: String, userSession: String, stockCode: String, period: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val getPerBandReq = GetPerBandReq(userId,userSession, stockCode, period)

            getPerDataUseCase.invoke(getPerBandReq).collect(){resource ->
                resource.let {
                    getPerDataResult.postValue(it)
                }
            }
        }
    }

    fun getPbvBand(userId: String, userSession: String, stockCode: String, period: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val getPbvBandReq = GetPbvBandReq(userId,userSession, stockCode, period)

            getPbvBandUseCase.invoke(getPbvBandReq).collect(){resource ->
                resource.let {
                    getPbvBandResult.postValue(it)
                }
            }
        }
    }

    fun getPbvData(userId: String, userSession: String, stockCode: String, period: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val getPbvBandReq = GetPbvBandReq(userId,userSession, stockCode, period)

            getPbvDataUseCase.invoke(getPbvBandReq).collect(){resource ->
                resource.let {
                    getPbvDataResult.postValue(it)
                }
            }
        }
    }

    fun getStockAnalysisRating(userId: String, userSession: String, stockCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val stockAnalysisRatingReq = StockAnalysisRatingReq(userId,userSession, stockCode)

            stockAnalysisRatingUseCase.invoke(stockAnalysisRatingReq).collect(){resource ->
                resource.let {
                    getStockAnalysisRatingResult.postValue(it)
                }
            }
        }
    }

    fun getFibonacciPivotPoint(userId: String, userSession: String, stockCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val fibonacciPivotPointReq = FibonacciPivotPointReq(userId,userSession, stockCode)

            fibonacciPivotPointUseCase.invoke(fibonacciPivotPointReq).collect(){resource ->
                resource.let {
                    getFibonacciPivotPointResult.postValue(it)
                }
            }
        }
    }
}