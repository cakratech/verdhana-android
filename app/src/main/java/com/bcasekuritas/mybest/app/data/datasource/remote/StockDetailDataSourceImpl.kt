package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.StockDetailDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.EarningsPerShareReq
import com.bcasekuritas.mybest.app.domain.dto.request.KeyStatRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockInfoDetailRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockOrderbookRequest
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageRequest
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.datafeed.MIType
import com.bcasekuritas.rabbitmq.proto.news.EarningsPerShareRequest
import com.bcasekuritas.rabbitmq.proto.news.EarningsPerShareResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewIndexSectorRequest
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatRequest
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatsRTIRequest
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatsRTIResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewStockInfoDetilRequest
import com.bcasekuritas.rabbitmq.proto.news.ViewStockInfoDetilResponse
import com.google.protobuf.GeneratedMessage
import javax.inject.Inject

class StockDetailDataSourceImpl @Inject constructor(
    private val oltService: OLTService
) : StockDetailDataSource {
    override suspend fun getCurrentMessage(currentMessageRequest: StockOrderbookRequest?): CurrentMessageResponse? {
        val currentMessageReq = CurrentMessageRequest
            .newBuilder()
            .setUserId(currentMessageRequest?.userId)
            .setSessionId(currentMessageRequest?.sessionId)
            .setDataType(MIType.ORDERBOOK_SUMMARY_COMPACT)
            .setBoardCode(currentMessageRequest?.boardCode)
            .addAllItemCode(currentMessageRequest?.stockCodeList)
            .build()

        return oltService.getOrderBookSummaryCurrentMessage(currentMessageReq)
    }

    override suspend fun startOrderBook() {
        oltService.startOrderBook()
    }

    override suspend fun setListenerOrderBook(miListener: MQMessageListener<MIMessage>?) {
        oltService.setListenerOrderBook(miListener)
    }

    override suspend fun subscribeOrderBook(routingKey: String?) {
        oltService.subscribeOrderBook(routingKey)
    }

    override suspend fun unsubscribeOrderBook(routingKey: String?) {
        oltService.unsubscribeOrderBook(routingKey)
    }

    override suspend fun stopOrderBook() {
        oltService.stopOrderBook()
    }

    override suspend fun getKeyStat(keyStatRequest: KeyStatRequest): ViewKeyStatResponse? {
        val viewKeyStatRequest = ViewKeyStatRequest
            .newBuilder()
            .setUserId(keyStatRequest.userId)
            .setSessionId(keyStatRequest.sessionId)
            .setStockCode(keyStatRequest.stockCode)
            .build()

        return oltService.getKeyStat(viewKeyStatRequest)
    }

    override suspend fun getKeyStatRti(keyStatRequest: KeyStatRequest): ViewKeyStatsRTIResponse? {
        val viewKeyStatsRTIRequest = ViewKeyStatsRTIRequest
            .newBuilder()
            .setUserId(keyStatRequest.userId)
            .setSessionId(keyStatRequest.sessionId)
            .setStockCode(keyStatRequest.stockCode)
            .build()

        return oltService.getKeyStatsRti(viewKeyStatsRTIRequest)
    }

    override suspend fun getEarningPerShares(earningsPerShareReq: EarningsPerShareReq): EarningsPerShareResponse? {
        val earningsPerShareRequest = EarningsPerShareRequest
            .newBuilder()
            .setUserId(earningsPerShareReq.userId)
            .setSessionId(earningsPerShareReq.sessionId)
            .setStockCode(earningsPerShareReq.stockCode)
            .build()

        return oltService.getEarningPerShares(earningsPerShareRequest)
    }

    override suspend fun getStockInfoDetail(stockInfoDetilRequest: StockInfoDetailRequest): ViewStockInfoDetilResponse? {
        val viewStockInfoDetilRequest = ViewStockInfoDetilRequest
            .newBuilder()
            .setUserId(stockInfoDetilRequest.userId)
            .setSessionId(stockInfoDetilRequest.sessionId)
            .setStockCode(stockInfoDetilRequest.stockCode)
            .build()

        return oltService.getStockInfoDetail(viewStockInfoDetilRequest)
    }


}