package com.bcasekuritas.mybest.app.feature.fastorder.setupvolume

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.MaxOrderByStockReq
import com.bcasekuritas.mybest.app.domain.interactors.GetMaxOrderByStockUseCase
import com.bcasekuritas.mybest.app.domain.interactors.InputVolumeUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.MaxOrderByStockResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SetupVolumeViewModel @Inject constructor(
    private val inputVolumeUseCase: InputVolumeUseCase,
    private val getMaxOrderByStockUseCase: GetMaxOrderByStockUseCase,
) : BaseViewModel() {

    val getMaxOrderByStockResult = MutableLiveData<Resource<MaxOrderByStockResponse?>>()

    fun getMaxOrder(userId: String, accNo: String, buySell: String, sessionId: String, stockCode: String,
                    price: Double, compare: String, boardCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val maxOrderByStockReq = MaxOrderByStockReq(
                userId = userId,
                accNo = accNo,
                buySell = buySell,
                stockCode = stockCode,
                price = price,
                buyType = compare,
                boardCode = boardCode,
                sessionId = sessionId)

            getMaxOrderByStockUseCase.invoke(maxOrderByStockReq).collect() {resource ->
                resource.let {
                    withContext(Dispatchers.Main) {
                        getMaxOrderByStockResult.postValue(it)
                    }
                }
            }
        }
    }

//    fun inputPin(listVolume: ArrayList<String>, pinInput: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val res = inputVolumeUseCase.invoke(listPin, pinInput)
//            when (res) {
//                is Resource.Success -> {
//                    inputPinResult.postValue(res)
//                }
//
//                else -> {
//
//                }
//            }
//        }
//    }
}