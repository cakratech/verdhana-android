package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.FastOrderDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.FastOrderListReq
import com.bcasekuritas.mybest.app.domain.dto.request.PublishFastOrderReq
import com.bcasekuritas.mybest.app.domain.repositories.FastOrderRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import com.bcasekuritas.rabbitmq.proto.bcas.FastOrderListResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FastOrderRepoImpl @Inject constructor(
    private val remoteSource: FastOrderDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : FastOrderRepo {
    override suspend fun getFastOrderList(fastOrderListReq: FastOrderListReq): Flow<Resource<FastOrderListResponse?>> =
        flow {
            emit(
                Resource.Success(
                    data = remoteSource.getFastOrderList(fastOrderListReq),
                    DataSource.REMOTE
                )
            )
        }

    override suspend fun publishFastOrder(publishFastOrderReq: PublishFastOrderReq?) {
        remoteSource.publishFastOrder(publishFastOrderReq)
    }
}