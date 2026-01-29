package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.domain.dto.request.NotificationHistoryReq
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.NotificationHistoryResponse
import kotlinx.coroutines.flow.Flow

interface NotificationRepo {

    suspend fun getNotificationHistory(notificationHistoryReq: NotificationHistoryReq): Flow<Resource<NotificationHistoryResponse?>>
}