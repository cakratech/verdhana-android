package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.domain.dto.request.EarningsPerShareReq
import com.bcasekuritas.mybest.app.domain.dto.request.KeyStatRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockInfoDetailRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockOrderbookRequest
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.news.EarningsPerShareResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatsRTIResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewStockInfoDetilResponse
import com.google.protobuf.GeneratedMessage

interface StockDetailDataSource {
    suspend fun getCurrentMessage(currentMessageRequest: StockOrderbookRequest?): CurrentMessageResponse?

    suspend fun startOrderBook()

    suspend fun setListenerOrderBook(miListener: MQMessageListener<MIMessage>?)

    suspend fun subscribeOrderBook(routingKey: String?)

    suspend fun unsubscribeOrderBook(routingKey: String?)

    suspend fun stopOrderBook()

    suspend fun getKeyStat(keyStatRequest: KeyStatRequest): ViewKeyStatResponse?

    suspend fun getKeyStatRti(keyStatRequest: KeyStatRequest): ViewKeyStatsRTIResponse?

    suspend fun getEarningPerShares(earningsPerShareRequest: EarningsPerShareReq): EarningsPerShareResponse?

    suspend fun getStockInfoDetail(stockInfoDetilRequest: StockInfoDetailRequest): ViewStockInfoDetilResponse?
}