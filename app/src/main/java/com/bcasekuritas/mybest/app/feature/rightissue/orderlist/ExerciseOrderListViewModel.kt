package com.bcasekuritas.mybest.app.feature.rightissue.orderlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.ExerciseOrderListReq
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawExerciseOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.response.ExerciseOrderListItem
import com.bcasekuritas.mybest.app.domain.interactors.GetExerciseOrderListUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetIpAddressUseCase
import com.bcasekuritas.mybest.app.domain.interactors.SendWithdrawExerciseOrderUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseOrderListViewModel @Inject constructor(
    private val exerciseOrderListUseCase: GetExerciseOrderListUseCase,
    private val sendWithdrawExerciseOrderUseCase: SendWithdrawExerciseOrderUseCase,
    private val getIpAddressUseCase: GetIpAddressUseCase
): BaseViewModel(){
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

    val exerciseOrderListResult = MutableLiveData<List<ExerciseOrderListItem>?>()

    fun getExerciseOrderList(reqType: String, reqInfo: List<String>, userId: String, sessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val exerciseOrderListReq = ExerciseOrderListReq(reqType, reqInfo, userId, sessionId)

            exerciseOrderListUseCase.invoke(exerciseOrderListReq).collect() {resource ->
                when (resource) {
                    is Resource.Success -> {
                        val item = resource.data?.exerciseOrderListInfoList?.map {
                            ExerciseOrderListItem(
                                it.transCode,
                                it.transDate,
                                it.transType,
                                it.accno,
                                it.accInit,
                                it.accName,
                                it.stockCode,
                                it.orderPrice,
                                it.orderQty,
                                it.amount,
                                it.status,
                                it.clOrderId,
                                it.salesId,
                                it.aogroupid,
                                it.inputby,
                                it.inputIpaddress,
                                it.remarks,
                                it.rejectReason,
                                it.channel,
                                it.mediaSource,
                                it.flag,
                                it.isRead,
                                it.createdBy,
                                it.createdDate,
                                it.lastModifiedBy,
                                it.lastModifiedDate
                            )
                        }
                        exerciseOrderListResult.postValue(item)
                    }
                    else -> {}
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