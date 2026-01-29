package com.bcasekuritas.mybest.app.feature.rightissue.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.StockWithNotationObject
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawExerciseOrderRequest
import com.bcasekuritas.mybest.app.domain.interactors.GetIpAddressUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetListStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SendWithdrawExerciseOrderUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    private val getStockParamDaoUseCase: GetListStockParamDaoUseCase,
    private val sendWithdrawExerciseOrderUseCase: SendWithdrawExerciseOrderUseCase,
    private val getIpAddressUseCase: GetIpAddressUseCase
): BaseViewModel() {

    var getStockParamResult = MutableLiveData<StockWithNotationObject?>()
    val getIpAddressResult = MutableLiveData<String>()

    fun getIpAddress() {
        viewModelScope.launch(Dispatchers.IO) {
            getIpAddressUseCase.invoke().collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        getIpAddressResult.postValue(resource.data?:"")
                    }
                    else -> {}
                }
            }
        }
    }

    fun getStockParam(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getStockParamDaoUseCase(listOf(value)).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { data ->
                            withContext(Dispatchers.Main){
                                getStockParamResult.postValue(data.get(0))
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

    fun sendWithdraw(withdrawExerciseOrderRequest: WithdrawExerciseOrderRequest){
        viewModelScope.launch(Dispatchers.IO) {
            sendWithdrawExerciseOrderUseCase.sendWithdraw(withdrawExerciseOrderRequest)
        }
    }
}