package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.domain.dto.request.GlobalMarketReq
import com.bcasekuritas.mybest.app.domain.dto.request.GlobalRankReq
import com.bcasekuritas.mybest.app.domain.dto.request.IndiceDataRequest
import com.bcasekuritas.mybest.app.domain.dto.request.MarketSessionReq
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.bcas.MarketSessionResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityByInvType2DiscoverResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.news.LatestComoditiesResponse
import com.bcasekuritas.rabbitmq.proto.news.LatestCurrencyResponse
import com.bcasekuritas.rabbitmq.proto.news.LatestIndexResponse
import com.google.protobuf.GeneratedMessage

interface GlobalMarketDataSource {

    suspend fun getIndiceData(indiceDataRequest: IndiceDataRequest): CurrentMessageResponse?

    suspend fun startIndiceData()

    suspend fun setListenerIndiceData(miListener: MQMessageListener<MIMessage>?)

    suspend fun subscribeAllIndiceData(routingKeys: List<String>)

    suspend fun subscribeIndiceData(routingKey: String)

    suspend fun unsubscribeAllIndiceData(routingKeys: List<String>)

    suspend fun unSubscribeIndiceData(routingKey: String)

    suspend fun stopIndiceData()

    suspend fun getMarketSession(marketSessionRequest: MarketSessionReq): MarketSessionResponse?
    suspend fun getGlobalCommodities(globalMarketReq: GlobalMarketReq): LatestComoditiesResponse?
    suspend fun getGlobalCurrency(globalMarketReq: GlobalMarketReq): LatestCurrencyResponse?
    suspend fun getGlobalIndex(globalMarketReq: GlobalMarketReq): LatestIndexResponse?
    suspend fun getGlobalRank(globalRankReq: GlobalRankReq): BrokerRankActivityByInvType2DiscoverResponse?

}