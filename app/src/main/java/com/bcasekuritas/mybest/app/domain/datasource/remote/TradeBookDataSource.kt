package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.domain.dto.request.TradeBookRequest
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeBookResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeBookTimeResponse

interface TradeBookDataSource {
    suspend fun getTradeBook(tradeBookRequest: TradeBookRequest?): TradeBookResponse?
    suspend fun getTradeBookTime(tradeBookTimeRequest: TradeBookRequest?): TradeBookTimeResponse?
}