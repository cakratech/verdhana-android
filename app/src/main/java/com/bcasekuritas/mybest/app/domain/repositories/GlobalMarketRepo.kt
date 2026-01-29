package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.domain.dto.request.GlobalMarketReq
import com.bcasekuritas.mybest.app.domain.dto.request.GlobalRankReq
import com.bcasekuritas.mybest.app.domain.dto.request.IndiceDataRequest
import com.bcasekuritas.mybest.app.domain.dto.request.MarketSessionReq
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
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

interface GlobalMarketRepo {

    suspend fun getIndiceData(indiceDataRequest: IndiceDataRequest): Flow<Resource<CurrentMessageResponse?>>

    suspend fun startIndiceData()

    suspend fun setListenerIndiceData(miListener: MQMessageListener<MIMessage>?)

    suspend fun subscribeAllIndiceData(routingKeys: List<String>)

    suspend fun subscribeIndiceData(routingKey: String)

    suspend fun unsubscribeAllIndiceData(routingKeys: List<String>)

    suspend fun unSubscribeIndiceData(routingKey: String)

    suspend fun stopIndiceData()

    suspend fun getMarketSession(marketSessionReq: MarketSessionReq): Flow<Resource<MarketSessionResponse?>>
    suspend fun getGlobalCommodities(globalMarketReq: GlobalMarketReq): Flow<Resource<LatestComoditiesResponse?>>
    suspend fun getGlobalCurrency(globalMarketReq: GlobalMarketReq):  Flow<Resource<LatestCurrencyResponse?>>
    suspend fun getGlobalIndex(globalMarketReq: GlobalMarketReq): Flow<Resource<LatestIndexResponse?>>
    suspend fun getGlobalRank(globalRankReq: GlobalRankReq): Flow<Resource<BrokerRankActivityByInvType2DiscoverResponse?>>
}