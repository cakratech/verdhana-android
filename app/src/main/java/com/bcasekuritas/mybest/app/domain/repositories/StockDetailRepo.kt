package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.domain.dto.request.EarningsPerShareReq
import com.bcasekuritas.mybest.app.domain.dto.request.KeyStatRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockInfoDetailRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockOrderbookRequest
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.news.EarningsPerShareResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewKeyStatsRTIResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewStockInfoDetilResponse
import kotlinx.coroutines.flow.Flow

interface StockDetailRepo {
    suspend fun getStockOrderbook(stockOrderbookRequest: StockOrderbookRequest?): Flow<Resource<CurrentMessageResponse?>>

    suspend fun startOrderBook()

    suspend fun setListenerOrderBook(miListener: MQMessageListener<MIMessage>?)

    suspend fun subscribeOrderBook(routingKey: String?)

    suspend fun unsubscribeOrderBook(routingKey: String?)

    suspend fun stopOrderBook()

    suspend fun getKeyStat(keyStatRequest: KeyStatRequest): Flow<Resource<ViewKeyStatResponse?>>

    suspend fun getKeyStatsRti(keyStatRequest: KeyStatRequest): Flow<Resource<ViewKeyStatsRTIResponse?>>

    suspend fun getEarningPerShare(earningsPerShareReq: EarningsPerShareReq): Flow<Resource<EarningsPerShareResponse?>>

    suspend fun getStockInfoDetail(stockInfoDetilRequest: StockInfoDetailRequest): Flow<Resource<ViewStockInfoDetilResponse?>>
}