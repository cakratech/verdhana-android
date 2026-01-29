package com.bcasekuritas.mybest.app.feature.dialog.order.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.MarketSessionReq
import com.bcasekuritas.mybest.app.domain.interactors.GetMarketSessionUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.MarketSessionInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DialogOrderBuyViewModel @Inject constructor(
    private val getMarketSessionUseCase: GetMarketSessionUseCase
) : BaseViewModel() {

    val getMarketSessionResult = MutableLiveData<MarketSessionInfo>()

    fun getMarketSession(userId: String){
        viewModelScope.launch(Dispatchers.IO) {
            val marketSessionReq = MarketSessionReq(userId)

            getMarketSessionUseCase.invoke(marketSessionReq).collect(){ res ->
                when (res) {
                    is Resource.Success -> {
                        getMarketSessionResult.postValue(res.data?.marketSessionInfo)
                    }
                    else -> {

                    }
                }
            }
        }
    }
}