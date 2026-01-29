package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.GlobalMarketDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.GlobalMarketReq
import com.bcasekuritas.mybest.app.domain.dto.request.GlobalRankReq
import com.bcasekuritas.mybest.app.domain.dto.request.IndiceDataRequest
import com.bcasekuritas.mybest.app.domain.dto.request.MarketSessionReq
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.bcas.MarketSessionRequest
import com.bcasekuritas.rabbitmq.proto.bcas.MarketSessionResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityByInvType2DiscoverRequest
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityByInvType2DiscoverResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageRequest
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.datafeed.MIType
import com.bcasekuritas.rabbitmq.proto.news.LatestComoditiesRequest
import com.bcasekuritas.rabbitmq.proto.news.LatestComoditiesResponse
import com.bcasekuritas.rabbitmq.proto.news.LatestCurrencyRequest
import com.bcasekuritas.rabbitmq.proto.news.LatestCurrencyResponse
import com.bcasekuritas.rabbitmq.proto.news.LatestIndexRequest
import com.bcasekuritas.rabbitmq.proto.news.LatestIndexResponse
import com.google.protobuf.GeneratedMessage
import javax.inject.Inject

class GlobalMarketDataSourceImpl @Inject constructor(
    private val oltService: OLTService
) : GlobalMarketDataSource {

    override suspend fun getIndiceData(indiceDataRequest: IndiceDataRequest): CurrentMessageResponse? {
        val indiceDataReq = CurrentMessageRequest.newBuilder()
            .setUserId(indiceDataRequest.userId)
            .setSessionId(indiceDataRequest.sessionId)
            .setDataType(MIType.INDICE_SUMMARY)
            .addItemCode(indiceDataRequest.indiceCode)
            .build()
        return oltService.getIndiceSummaryCurrentMessage(indiceDataReq)
    }

    override suspend fun startIndiceData() {
        oltService.startIndiceData()
    }

    override suspend fun setListenerIndiceData(miListener: MQMessageListener<MIMessage>?) {
        oltService.setListenerIndiceData(miListener)
    }

    override suspend fun subscribeAllIndiceData(routingKeys: List<String>) {
        oltService.subscribeAllIndiceData(routingKeys)
    }

    override suspend fun subscribeIndiceData(routingKey: String) {
        oltService.subscribeIndiceData(routingKey)
    }

    override suspend fun unsubscribeAllIndiceData(routingKeys: List<String>) {
        oltService.unsubscribeAllIndiceData(routingKeys)
    }

    override suspend fun unSubscribeIndiceData(routingKey: String) {
        oltService.unsubscribeIndiceData(routingKey)
    }

    override suspend fun stopIndiceData() {
        oltService.stopIndiceData()
    }

    override suspend fun getMarketSession(marketSessionRequest: MarketSessionReq): MarketSessionResponse? {
        val marketSessionReq = MarketSessionRequest.newBuilder()
            .setUserId(marketSessionRequest.userId)
            .build()

        return oltService.getMarketSession(marketSessionReq)
    }

    override suspend fun getGlobalCommodities(globalMarketReq: GlobalMarketReq): LatestComoditiesResponse? {
        val commoditiesReq = LatestComoditiesRequest.newBuilder()
            .setUserId(globalMarketReq.userId)
            .setSessionId(globalMarketReq.sessionId)
            .build()

        return oltService.getGlobalCommodities(commoditiesReq)
    }

    override suspend fun getGlobalCurrency(globalMarketReq: GlobalMarketReq): LatestCurrencyResponse? {
        val currencyReq = LatestCurrencyRequest.newBuilder()
            .setUserId(globalMarketReq.userId)
            .setSessionId(globalMarketReq.sessionId)
            .build()

        return oltService.getGlobalCurrency(currencyReq)
    }

    override suspend fun getGlobalIndex(globalMarketReq: GlobalMarketReq): LatestIndexResponse? {
        val indexReq = LatestIndexRequest.newBuilder()
            .setUserId(globalMarketReq.userId)
            .setSessionId(globalMarketReq.sessionId)
            .build()

        return oltService.getGlobalIndex(indexReq)
    }

    override suspend fun getGlobalRank(globalRankReq: GlobalRankReq): BrokerRankActivityByInvType2DiscoverResponse? {
        val globalRankRequest = BrokerRankActivityByInvType2DiscoverRequest.newBuilder()
            .setUserId(globalRankReq.userId)
            .setSessionId(globalRankReq.sessionId)
            .setSortField(globalRankReq.activity)
            .setStartDate(globalRankReq.startDate)
            .setEndDate(globalRankReq.endDate)
            .setBoard(globalRankReq.board)
            .build()

        return oltService.getGlobalRank(globalRankRequest)
    }
}