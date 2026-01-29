package com.bcasekuritas.mybest.app.feature.notification.transactionnotification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.app.domain.dto.request.NotificationHistoryReq
import com.bcasekuritas.mybest.app.domain.interactors.GetNotificationHistoryUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener
import com.bcasekuritas.rabbitmq.proto.bcas.NotificationHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionNotificationViewModel @Inject constructor(
    private val notificationHistoryUseCase: GetNotificationHistoryUseCase,
    val imqConnectionListener: IMQConnectionListener
): BaseViewModel() {

    val isNotificationEmpty = MutableLiveData<Boolean>()
    val getNotificationHistoryResult = MutableLiveData<List<NotificationHistory>>()

    fun getNotificationHistory(userId: String, sessionId: String, page: Int, size: Int ) {
        viewModelScope.launch(Dispatchers.IO) {
            val notificationHistoryReq = NotificationHistoryReq(userId, sessionId, page, size)

            notificationHistoryUseCase.invoke(notificationHistoryReq).collect() {resource ->
                resource.let {
                    when (resource) {
                        is Resource.Success -> {
                            if (resource.data != null) {
                                if (resource.data.notificationHistoryCount != 0) {
                                    isNotificationEmpty.postValue(false)
                                    getNotificationHistoryResult.postValue(resource.data.notificationHistoryList)
                                } else if (resource.data.page.totalPages == 0){
                                    isNotificationEmpty.postValue(true)
                                }

                            } else {
                                isNotificationEmpty.postValue(true)
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}