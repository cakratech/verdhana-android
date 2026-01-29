package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.NotificationDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.NotificationHistoryReq
import com.bcasekuritas.mybest.app.domain.repositories.NotificationRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import com.bcasekuritas.rabbitmq.proto.bcas.NotificationHistoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NotificationRepoImpl @Inject constructor(
    private val remoteSource: NotificationDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : NotificationRepo {

    override suspend fun getNotificationHistory(notificationHistoryReq: NotificationHistoryReq): Flow<Resource<NotificationHistoryResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getNotificationHistory(notificationHistoryReq), DataSource.REMOTE))
    }
}