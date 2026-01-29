package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.data.entity.FilterRunningTradeObject
import com.bcasekuritas.mybest.app.domain.datasource.remote.RunningTradeDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.LatestTradeDetailRequest
import com.bcasekuritas.mybest.app.domain.repositories.RunningTradeRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.datafeed.LatestTradeDetailResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RunningTradeRepoImpl @Inject constructor(
    private val remoteSource: RunningTradeDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
): RunningTradeRepo {

    override suspend fun getLatestTradeDetail(latestTradeDetailRequest: LatestTradeDetailRequest): Flow<Resource<LatestTradeDetailResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getLatestTradeDetail(latestTradeDetailRequest), DataSource.REMOTE))
    }

    override suspend fun startRunningTrade() {
        remoteSource.startRunningTrade()
    }

    override suspend fun setListenerRunningTrade(miListener: MQMessageListener<MIMessage>?) {
        remoteSource.setListenerRunningTrade(miListener)
    }

    override suspend fun subscribeRunningTrade(routingKey: String?) {
        remoteSource.subscribeRunningTrade(routingKey)
    }

    override suspend fun unsubscribeRunningTrade(stockCode: String?) {
        remoteSource.unsubscribeRunningTrade(stockCode)
    }

    override suspend fun stopRunningTrade() {
        remoteSource.stopRunningTrade()
    }

    override suspend fun setDefaultFilter(filterRunningTrade: FilterRunningTradeObject) {
       remoteSource.setDefaultFilter(filterRunningTrade)
    }

    override suspend fun getDefaultFilter(userId: String): Flow<Resource<FilterRunningTradeObject>> = flow {
        emit(Resource.Success(data = remoteSource.getDefaultFilter(userId), DataSource.REMOTE))
    }

    override suspend fun resetDefaultFilter(userId: String) {
        remoteSource.resetDefaultFilter(userId)
    }
}