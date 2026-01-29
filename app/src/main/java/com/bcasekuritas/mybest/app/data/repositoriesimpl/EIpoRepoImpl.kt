package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.EIpoDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.IPOInfoRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IPOListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IPOOrderListRequest
import com.bcasekuritas.mybest.app.domain.repositories.EIpoRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import com.bcasekuritas.rabbitmq.proto.bcas.IpoOrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.PipelinesIpoInfoResponse
import com.bcasekuritas.rabbitmq.proto.bcas.PipelinesIpoListResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EIpoRepoImpl @Inject constructor(
    private val remoteSource: EIpoDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : EIpoRepo {

    override suspend fun getIPOList(ipoListRequest: IPOListRequest): Flow<Resource<PipelinesIpoListResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getIPOList(ipoListRequest), DataSource.REMOTE))
    }

    override suspend fun getIPOInfo(ipoInfoRequest: IPOInfoRequest): Flow<Resource<PipelinesIpoInfoResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getIPOInfo(ipoInfoRequest), DataSource.REMOTE))
    }

    override suspend fun getIPOOrderList(ipoOrderListRequest: IPOOrderListRequest): Flow<Resource<IpoOrderListResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getIPOOrderList(ipoOrderListRequest), DataSource.REMOTE))
    }
}