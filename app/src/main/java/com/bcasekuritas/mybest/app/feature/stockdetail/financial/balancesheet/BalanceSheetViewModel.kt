package com.bcasekuritas.mybest.app.feature.stockdetail.financial.balancesheet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.DetailFinancialRequest
import com.bcasekuritas.mybest.app.domain.interactors.GetDetailBalanceSheetUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetDetailIncomeStatementUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.news.FinancialBalanceSheetResponse
import com.bcasekuritas.rabbitmq.proto.news.FinancialIncomeStatementResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BalanceSheetViewModel @Inject constructor(
    private val getDetailBalanceSheetUseCase: GetDetailBalanceSheetUseCase
) : BaseViewModel() {
    val getDetailBalanceSheetResult = MutableLiveData<Resource<FinancialBalanceSheetResponse?>>()

    fun getDetailBalanceSheet(userId: String, sessionId: String, stockCode: String, periodRange: Int, periodType: Int){
        viewModelScope.launch(Dispatchers.IO) {
            val financialRequest = DetailFinancialRequest(userId, sessionId, stockCode, periodRange, periodType)

            getDetailBalanceSheetUseCase.invoke(financialRequest).collect{resource ->
                resource.let {
                    withContext(Dispatchers.Main){
                        getDetailBalanceSheetResult.postValue(it)
                        Timber.d("balance : $it")
                    }
                }
            }
        }
    }
}
