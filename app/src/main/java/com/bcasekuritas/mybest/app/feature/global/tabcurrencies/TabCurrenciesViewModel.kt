package com.bcasekuritas.mybest.app.feature.global.tabcurrencies

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.GlobalMarketReq
import com.bcasekuritas.mybest.app.domain.interactors.GetGlobalCurrencyUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.proto.news.LatestCurrencyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TabCurrenciesViewModel @Inject constructor(
    private val getGlobalCurrencyUseCase: GetGlobalCurrencyUseCase,
    val imqConnectionListener: IMQConnectionListener
) : BaseViewModel() {

    var getGlobalCurrencyResult = MutableLiveData<Resource<LatestCurrencyResponse?>>()

    fun getGlobalCurrency(userId: String, sessionId: String){
        viewModelScope.launch(Dispatchers.IO) {
            val globalCurrencyReq = GlobalMarketReq(userId, sessionId)

            getGlobalCurrencyUseCase.invoke(globalCurrencyReq).collect() {resource ->
                resource.let {
                    getGlobalCurrencyResult.postValue(it)
                }
            }
        }
    }
}