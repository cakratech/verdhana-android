package com.bcasekuritas.mybest.app.feature.selectaccount

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.AccountObject
import com.bcasekuritas.mybest.app.domain.dto.request.CashPosRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockParamListRequest
import com.bcasekuritas.mybest.app.domain.dto.response.source.StockNotationRes
import com.bcasekuritas.mybest.app.domain.dto.response.source.StockParamRes
import com.bcasekuritas.mybest.app.domain.interactors.GetCashPosUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockParamListUseCase
import com.bcasekuritas.mybest.app.domain.interactors.InsertAccountDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.InsertStockNotationDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.InsertStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.CIFCashPosResponse
import com.bcasekuritas.rabbitmq.proto.bcas.StockParamResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SelectAccountViewModel  @Inject constructor(
    private val getCashPosUseCase: GetCashPosUseCase,
    private val insertAccountDaoUseCase: InsertAccountDaoUseCase,
    private val insertStockParamUseCase: InsertStockParamDaoUseCase,
    private val getStockParamListUseCase: GetStockParamListUseCase,
    private val insertStockNotationDaoUseCase: InsertStockNotationDaoUseCase
): BaseViewModel() {
    val getCashPosResult = MutableLiveData<Resource<CIFCashPosResponse?>>()
    val getStockParamListResult = MutableLiveData<Resource<StockParamResponse?>>()

    fun getCashPos(userId: String, cifCode: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val cashPosRequest = CashPosRequest(userId, cifCode, 1, sessionId)

            getCashPosUseCase.invoke(cashPosRequest).collect() {resource ->
                resource.let {
                    getCashPosResult.postValue(it)
                }
            }
        }

    }

    fun insertAccountDao(accountRes: AccountObject) {
        viewModelScope.launch(Dispatchers.IO) {
            insertAccountDaoUseCase.insertAccountDao(accountRes)
        }
    }

    fun getStockParamList(userId: String, sessionId: String) {
        val stockParamReq = StockParamListRequest(userId, sessionId, "IDX", "*", System.currentTimeMillis().div(1000))
        viewModelScope.launch(Dispatchers.IO) {
            getStockParamListUseCase.invoke(stockParamReq).collect(){ resource ->
                resource.let {
                    getStockParamListResult.postValue(it)
                }
            }
        }
    }

    fun insertStockParamDao(stockParamRes: StockParamRes) {
        viewModelScope.launch(Dispatchers.IO) {
            insertStockParamUseCase.insertStockParamDao(stockParamRes)
        }
    }

    fun insertStockNotationDao(stockNotationRes: StockNotationRes) {
        viewModelScope.launch(Dispatchers.IO) {
            insertStockNotationDaoUseCase.insertStockNotationDao(stockNotationRes)
        }
    }
}