package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.NotificationDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.NotificationHistoryReq
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.bcas.NotificationHistoryRequest
import com.bcasekuritas.rabbitmq.proto.bcas.NotificationHistoryResponse
import com.bcasekuritas.rabbitmq.proto.bcas.PageRequest
import javax.inject.Inject

class NotificationDataSourceImpl @Inject constructor(
    private val oltService: OLTService
) : NotificationDataSource {

    override suspend fun getNotificationHistory(notificationHistoryReq: NotificationHistoryReq): NotificationHistoryResponse? {
        val pageRequest = PageRequest.newBuilder()
            .setPage(notificationHistoryReq.page)
            .setSize(notificationHistoryReq.size)
            .build()

        val notificationHistoryRequest = NotificationHistoryRequest.newBuilder()
            .setUserId(notificationHistoryReq.userId)
            .setSessionId(notificationHistoryReq.sessionId)
            .setPageRequest(pageRequest)
            .build()

        return oltService.getNotificationHistory(notificationHistoryRequest)
    }
}