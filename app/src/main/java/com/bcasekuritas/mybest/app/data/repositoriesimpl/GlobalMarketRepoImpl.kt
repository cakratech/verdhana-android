package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.GlobalMarketDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.GlobalMarketReq
import com.bcasekuritas.mybest.app.domain.dto.request.GlobalRankReq
import com.bcasekuritas.mybest.app.domain.dto.request.IndiceDataRequest
import com.bcasekuritas.mybest.app.domain.dto.request.MarketSessionReq
import com.bcasekuritas.mybest.app.domain.repositories.GlobalMarketRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.bcas.MarketSessionResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityByInvType2DiscoverResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.news.LatestComoditiesResponse
import com.bcasekuritas.rabbitmq.proto.news.LatestCurrencyResponse
import com.bcasekuritas.rabbitmq.proto.news.LatestIndexResponse
import com.google.protobuf.GeneratedMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GlobalMarketRepoImpl @Inject constructor(
    private val remoteSource: GlobalMarketDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : GlobalMarketRepo {

    override suspend fun getIndiceData(indiceDataRequest: IndiceDataRequest): Flow<Resource<CurrentMessageResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getIndiceData(indiceDataRequest), DataSource.REMOTE))
    }

    override suspend fun startIndiceData() {
        remoteSource.startIndiceData()
    }

    override suspend fun setListenerIndiceData(miListener: MQMessageListener<MIMessage>?) {
        remoteSource.setListenerIndiceData(miListener)
    }

    override suspend fun subscribeAllIndiceData(routingKeys: List<String>) {
        remoteSource.subscribeAllIndiceData(routingKeys)
    }

    override suspend fun subscribeIndiceData(routingKey: String) {
        remoteSource.subscribeIndiceData(routingKey)
    }

    override suspend fun unsubscribeAllIndiceData(routingKeys: List<String>) {
        remoteSource.unsubscribeAllIndiceData(routingKeys)
    }

    override suspend fun unSubscribeIndiceData(routingKey: String) {
        remoteSource.unSubscribeIndiceData(routingKey)
    }

    override suspend fun stopIndiceData() {
        remoteSource.stopIndiceData()
    }

    override suspend fun getMarketSession(marketSessionReq: MarketSessionReq): Flow<Resource<MarketSessionResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getMarketSession(marketSessionReq), DataSource.REMOTE))
    }

    override suspend fun getGlobalCommodities(globalMarketReq: GlobalMarketReq): Flow<Resource<LatestComoditiesResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getGlobalCommodities(globalMarketReq), DataSource.REMOTE))
    }

    override suspend fun getGlobalCurrency(globalMarketReq: GlobalMarketReq): Flow<Resource<LatestCurrencyResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getGlobalCurrency(globalMarketReq), DataSource.REMOTE))
    }

    override suspend fun getGlobalIndex(globalMarketReq: GlobalMarketReq): Flow<Resource<LatestIndexResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getGlobalIndex(globalMarketReq), DataSource.REMOTE))
    }

    override suspend fun getGlobalRank(globalRankReq: GlobalRankReq): Flow<Resource<BrokerRankActivityByInvType2DiscoverResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getGlobalRank(globalRankReq), DataSource.REMOTE))
    }
}