package com.bcasekuritas.mybest.app.feature.stockdetail.financial.overview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.FinancialRequest
import com.bcasekuritas.mybest.app.domain.interactors.GetBalanceSheetUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetCashFlowUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetIncomeStatementUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.news.ViewBalanceSheetResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewCashFlowResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewIncomeStatementResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FinancialOverviewViewModel @Inject constructor(
    private val getIncomeStatementUseCase: GetIncomeStatementUseCase,
    private val getBalanceSheetUseCase: GetBalanceSheetUseCase,
    private val getCashFlowUseCase: GetCashFlowUseCase
) : BaseViewModel() {
    val getIncomeStatementResult = MutableLiveData<Resource<ViewIncomeStatementResponse?>>()
    val getBalanceSheetResult = MutableLiveData<Resource<ViewBalanceSheetResponse?>>()
    val getCashFlowResult = MutableLiveData<Resource<ViewCashFlowResponse?>>()

    fun getIncomeStatementChart(userId: String, sessionId: String, stockCode: String){
        viewModelScope.launch(Dispatchers.IO) {
            val financialRequest = FinancialRequest(userId, sessionId, stockCode)

            getIncomeStatementUseCase.invoke(financialRequest).collect(){resource ->
                resource.let {
                    getIncomeStatementResult.postValue(it)
                }
            }
        }
    }

    fun getBalanceSheetChart(userId: String, sessionId: String, stockCode: String){
        viewModelScope.launch(Dispatchers.IO) {
            val financialRequest = FinancialRequest(userId, sessionId, stockCode)

            getBalanceSheetUseCase.invoke(financialRequest).collect(){resource ->
                resource.let {
                    getBalanceSheetResult.postValue(it)
                }
            }
        }
    }
    fun getCashFlowChart(userId: String, sessionId: String, stockCode: String){
        viewModelScope.launch(Dispatchers.IO) {
            val financialRequest = FinancialRequest(userId, sessionId, stockCode)

            getCashFlowUseCase.invoke(financialRequest).collect(){resource ->
                resource.let {
                    getCashFlowResult.postValue(it)
                }
            }
        }
    }
}