package com.bcasekuritas.mybest.app.feature.e_ipo.eipodetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.data.entity.StockParamObject
import com.bcasekuritas.mybest.app.data.mapper.toIpoData
import com.bcasekuritas.mybest.app.domain.dto.request.IPOInfoRequest
import com.bcasekuritas.mybest.app.domain.dto.response.IpoData
import com.bcasekuritas.mybest.app.domain.interactors.GetIPOInfoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.GetStockParamDaoUseCase
import com.bcasekuritas.mybest.app.domain.repositories.OrderRepo
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EIPODetailViewModel @Inject constructor(
    private val getStockParamDaoUseCase: GetStockParamDaoUseCase,
    private val getIPOInfoUseCase: GetIPOInfoUseCase,
    private val orderRepo: OrderRepo
): BaseViewModel() {
    var getStockParamResult = MutableLiveData<StockParamObject?>()
    val getIpoInfoResult = MutableLiveData<IpoData>()
    val getIpoInfoForOrderResult = MutableLiveData<IpoData>()

    val orderEipoLiveData: LiveData<String?>
        get() = orderRepo.getOrderEipo as MutableLiveData

    fun clearOrderReply() {
        (orderEipoLiveData as MutableLiveData).value = null
    }

    fun getIpoInfo(userId: String, sessionId: String, ipoCode: String, isChecking: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = IPOInfoRequest(userId, sessionId, ipoCode)

            getIPOInfoUseCase.invoke(request).collect() {resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            val response = it.data?.pipelinesIpoListData
                            if (response != null) {
                                if (isChecking) {
                                    getIpoInfoForOrderResult.postValue(response.toIpoData())
                                } else {
                                    getIpoInfoResult.postValue(response.toIpoData())
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun getStockParam(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getStockParamDaoUseCase(value).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { data ->
                            withContext(Dispatchers.Main){
                                getStockParamResult.postValue(data)
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