package com.bcasekuritas.mybest.app.feature.rdn.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.PageRequest
import com.bcasekuritas.mybest.app.domain.dto.request.RdnHistoryRequest
import com.bcasekuritas.mybest.app.domain.interactors.GetRdnHistoryUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.proto.bcas.AccountCashMovementResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RdnHistoryViewModel @Inject constructor(
    private val getRdnHistoryUseCase: GetRdnHistoryUseCase,
    val imqConnectionListener: IMQConnectionListener
): BaseViewModel() {

    val rdnHistoryResult = MutableLiveData<AccountCashMovementResponse?>()

    fun getRdnHistory(userId: String, accNo: String, startDate: Long, endDate: Long, type: String, sessionId: String, page: Int) {
        viewModelScope.launch {

            val pageRequest = PageRequest(page, 10)
            val rdnHistoryRequest = RdnHistoryRequest(userId, accNo, startDate, endDate, type, sessionId, pageRequest)

            getRdnHistoryUseCase.invoke(rdnHistoryRequest).collect() {resource ->
                resource.let {
                    when (resource) {
                        is Resource.Success -> {
                            rdnHistoryResult.postValue(resource.data)
                        }
                        else -> {}
                    }
                }
            }
        }
    }
    
}