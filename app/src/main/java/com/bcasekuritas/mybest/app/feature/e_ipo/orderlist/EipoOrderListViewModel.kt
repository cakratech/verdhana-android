package com.bcasekuritas.mybest.app.feature.e_ipo.orderlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.IPOOrderListRequest
import com.bcasekuritas.mybest.app.domain.interactors.GetIPOOrderListUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.IpoOrderListData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EipoOrderListViewModel @Inject constructor(
    private val getIPOOrderListUseCase: GetIPOOrderListUseCase
): BaseViewModel() {

    val getEipoOrderListResult = MutableLiveData<List<IpoOrderListData>>()

    fun getEipoOrderList(userId: String, sessionId: String, accNo: String, ipoCode: String, page: Int, sizeItem: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = IPOOrderListRequest(
                userId,
                sessionId,
                accNo,
                ipoCode,
                11,
                sizeItem,
                page
            )

            getIPOOrderListUseCase.invoke(request).collect() {resource ->
                resource.let {
                    when (resource){
                        is Resource.Success -> {
                            if (resource.data != null) {
                                val eipoOrderList = resource.data.ipoListDataList

                               if (eipoOrderList.isNotEmpty()) {
                                   val sortData = eipoOrderList.sortedByDescending { it.createdAt }
                                   getEipoOrderListResult.postValue(sortData)
                               }
                            }
                        }
                        else -> {}
                    }
                }
            }
        }

    }
}