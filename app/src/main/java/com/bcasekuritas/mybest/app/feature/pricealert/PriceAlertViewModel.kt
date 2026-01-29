package com.bcasekuritas.mybest.app.feature.pricealert

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.PriceAlertReq
import com.bcasekuritas.mybest.app.domain.dto.request.RemovePriceAlertReq
import com.bcasekuritas.mybest.app.domain.dto.response.PriceAlertItem
import com.bcasekuritas.mybest.app.domain.interactors.GetListPriceAlertUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetListStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.RemovePriceAlertUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.proto.bcas.RemovePriceAlertResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PriceAlertViewModel @Inject constructor(
    private val getListPriceAlertUseCase: GetListPriceAlertUseCase,
    private val getListStockParamDaoUseCase: GetListStockParamDaoUseCase,
    private val removePriceAlertUseCase: RemovePriceAlertUseCase,
    val imqConnectionListener: IMQConnectionListener
): BaseViewModel() {

    private val priceAlertMap = ArrayList<PriceAlertItem>()
    val getListPriceAlertResult = MutableLiveData<List<PriceAlertItem>>()
    val isPriceAlertEmpty = MutableLiveData<Boolean>()
    val removePriceAlertResult = MutableLiveData<RemovePriceAlertResponse?>()

    fun getListPriceAlert(userId: String, sessionId: String, stockCode: String) {
        viewModelScope.launch {
            val priceAlertReq = PriceAlertReq(userId, sessionId, stockCode)

            getListPriceAlertUseCase.invoke(priceAlertReq).collect() {resource ->
                resource.let {
                    when (resource) {
                        is Resource.Success -> {
                            if (resource.data != null) {
                                when (resource.data.status) {
                                    0 -> {
                                        priceAlertMap.clear()
                                        val listData = resource.data.priceAlertList
                                        if (listData.isNotEmpty()) {

                                            val routingKey = listData.distinctBy { it.stockCode }.mapNotNull {it.stockCode}
                                            getListStockParam(routingKey)

                                            listData.forEach {item ->
                                                priceAlertMap.add(PriceAlertItem(
                                                    item.id,
                                                    item.stockCode,
                                                    operation = item.operation,
                                                    price = item.price,
                                                    status = item.status,
                                                    triggerAt = item.triggerAt
                                                ))
                                            }

                                            isPriceAlertEmpty.value = false
                                        } else {
                                            isPriceAlertEmpty.value = true
                                        }
                                    }
                                    else -> isPriceAlertEmpty.value = true
                                }
                            } else {
                                isPriceAlertEmpty.value = true
                            }


                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun deletePriceAlert(userId: String, sessionId: String, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val removePriceAlertRequest = RemovePriceAlertReq(userId, sessionId, id)

            removePriceAlertUseCase.invoke(removePriceAlertRequest).collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        removePriceAlertResult.postValue(resource.data)
                    }
                    else -> {}
                }
            }
        }
    }

    fun getListStockParam(value: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            getListStockParamDaoUseCase(value).collect { resource ->
                when (resource) {
                    is Resource.Success -> {

                        val updatedPriceAlert = arrayListOf<PriceAlertItem>()

                        resource.data?.let { data ->
//                            data.map {item ->
//                                updatedPriceAlert.map{priceAlert ->
//                                    if (priceAlert.stockCode == item?.stockParam?.stockCode){
//                                        priceAlert.copy(stockName = item.stockParam.stockName)
//                                    }
//                                }
//                            }

                            data.forEach { item ->
                                var stockName = ""
                                priceAlertMap.forEach {
                                    if (it.stockCode == item?.stockParam?.stockCode){
                                        stockName = item.stockParam.stockName
                                        updatedPriceAlert.add(PriceAlertItem(
                                            it.id,
                                            it.stockCode,
                                            stockName,
                                            operation = it.operation,
                                            price = it.price,
                                            status = it.status,
                                            triggerAt = it.triggerAt
                                        ))
                                    }
                                }

                            }
                            withContext(Dispatchers.Main) {
                                getListPriceAlertResult.postValue(updatedPriceAlert)
                            }
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