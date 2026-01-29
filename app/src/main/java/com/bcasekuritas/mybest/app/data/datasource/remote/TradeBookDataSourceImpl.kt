package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.TradeBookDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.TradeBookRequest
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeBookResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeBookTimeRequest
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeBookTimeResponse
import javax.inject.Inject

class TradeBookDataSourceImpl @Inject constructor(
    private val oltService: OLTService
) : TradeBookDataSource {
    override suspend fun getTradeBook(tradeBookRequest: TradeBookRequest?): TradeBookResponse? {
        val tradeBookReq = com.bcasekuritas.rabbitmq.proto.datafeed.TradeBookRequest
            .newBuilder()
            .setUserId(tradeBookRequest?.userId)
            .setSessionId(tradeBookRequest?.sessionId)
            .setSecCode(tradeBookRequest?.secCode)
            .build()

        return oltService.getTradeBook(tradeBookReq)
    }

    override suspend fun getTradeBookTime(tradeBookTimeRequest: TradeBookRequest?): TradeBookTimeResponse? {
        val tradeBookTimeRequest = TradeBookTimeRequest
            .newBuilder()
            .setUserId(tradeBookTimeRequest?.userId)
            .setSessionId(tradeBookTimeRequest?.sessionId)
            .setSecCode(tradeBookTimeRequest?.secCode)
            .build()

        return oltService.getTradeBookTime(tradeBookTimeRequest)
    }
}