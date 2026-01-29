package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.domain.dto.request.NotificationHistoryReq
import com.bcasekuritas.rabbitmq.proto.bcas.NotificationHistoryResponse

interface NotificationDataSource {

    suspend fun getNotificationHistory(notificationHistoryReq: NotificationHistoryReq): NotificationHistoryResponse?

}