package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.BrokerSumDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerRankActivityReq
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerRankByStockReq
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerSummaryRankingReq
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerStockSumRequest
import com.bcasekuritas.mybest.app.domain.repositories.BrokerSumRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerListResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockNetDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankingDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerStockSummaryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BrokerSumRepoImpl @Inject constructor(
    private val remoteSource: BrokerSumDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : BrokerSumRepo {
    override suspend fun getBrokerStockSummary(brokerStockSummaryRequest: BrokerStockSumRequest): Flow<Resource<BrokerStockSummaryResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getBrokerStockSummary(brokerStockSummaryRequest), DataSource.REMOTE))
    }

    override suspend fun getBrokerRankByStock(brokerRankByStockReq: BrokerRankByStockReq): Flow<Resource<BrokerRankByStockDiscoverResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getBrokerRankByStock(brokerRankByStockReq), DataSource.REMOTE))
    }

    override suspend fun getBrokerSummaryByStockNet(brokerRankByStockReq: BrokerRankByStockReq): Flow<Resource<BrokerRankByStockNetDiscoverResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getBrokerSummaryByStockNet(brokerRankByStockReq), DataSource.REMOTE))
    }

    override suspend fun getBrokerList(userId: String): Flow<Resource<BrokerListResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getBrokerList(userId), DataSource.REMOTE))
    }

    override suspend fun getBrokerRankActivity(brokerRankActivityReq: BrokerRankActivityReq): Flow<Resource<BrokerRankActivityDiscoverResponse?>> = flow{
        emit(Resource.Success(data = remoteSource.getBrokerRankActivity(brokerRankActivityReq), DataSource.REMOTE))
    }

    override suspend fun getBrokerRankRanking(brokerRankRankingReq: BrokerSummaryRankingReq): Flow<Resource<BrokerRankingDiscoverResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getBrokeRankRanking(brokerRankRankingReq), DataSource.REMOTE))
    }
}