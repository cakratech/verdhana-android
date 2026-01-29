package com.bcasekuritas.mybest.app.feature.stockdetail.financial.cashflow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.DetailFinancialRequest
import com.bcasekuritas.mybest.app.domain.interactors.GetDetailBalanceSheetUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetDetailCashFlowUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.news.FinancialBalanceSheetResponse
import com.bcasekuritas.rabbitmq.proto.news.FinancialCashFlowResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CashflowViewModel @Inject constructor(
    private val getDetailCashFlowUseCase: GetDetailCashFlowUseCase
) : BaseViewModel() {
    val getDetailCashFlowResult = MutableLiveData<Resource<FinancialCashFlowResponse?>>()

    fun getDetailCashFlow(userId: String, sessionId: String, stockCode: String, periodRange: Int, periodType: Int){
        viewModelScope.launch(Dispatchers.IO) {
            val financialRequest = DetailFinancialRequest(userId, sessionId, stockCode, periodRange, periodType)

            getDetailCashFlowUseCase.invoke(financialRequest).collect(){resource ->
                resource.let {
                    withContext(Dispatchers.Main){
                        getDetailCashFlowResult.postValue(it)
                    }
                }
            }
        }
    }
}