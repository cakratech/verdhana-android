package com.bcasekuritas.mybest.app.feature.stockdetail.financial.incomestatement

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.DetailFinancialRequest
import com.bcasekuritas.mybest.app.domain.dto.request.FinancialRequest
import com.bcasekuritas.mybest.app.domain.interactors.GetDetailIncomeStatementUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.news.FinancialIncomeStatementResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class IncomeStatementViewModel @Inject constructor(
    private val getDetailIncomeStatementUseCase: GetDetailIncomeStatementUseCase
) : BaseViewModel() {
    val getDetailncomeStatementResult = MutableLiveData<Resource<FinancialIncomeStatementResponse?>>()

    fun getDetailIncomeStatement(userId: String, sessionId: String, stockCode: String, periodRange: Int, periodType: Int){
        viewModelScope.launch(Dispatchers.IO) {
            val financialRequest = DetailFinancialRequest(userId, sessionId, stockCode, periodRange, periodType)

            getDetailIncomeStatementUseCase.invoke(financialRequest).collect(){resource ->
                resource.let {
                    withContext(Dispatchers.Main){
                        getDetailncomeStatementResult.postValue(it)
                    }
                }
            }
        }
    }
}