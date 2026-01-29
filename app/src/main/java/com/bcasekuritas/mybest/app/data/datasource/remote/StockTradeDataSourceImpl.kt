package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.StockTradeDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.StockTradeReq
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.datafeed.StockTradeRequest
import com.bcasekuritas.rabbitmq.proto.datafeed.StockTradeResponse
import javax.inject.Inject

class StockTradeDataSourceImpl  @Inject constructor(
    private val oltService: OLTService
) : StockTradeDataSource {
    override suspend fun getStockTrade(stockDetailTradeRequest: StockTradeReq?): StockTradeResponse? {
        val stockTradeRequest = StockTradeRequest
            .newBuilder()
            .setUserId(stockDetailTradeRequest?.userId)
            .setSessionId(stockDetailTradeRequest?.sessionId)
            .setSecCode(stockDetailTradeRequest?.secCode)
            .setAction(stockDetailTradeRequest!!.action)
            .setLimit(stockDetailTradeRequest.limit)
            .build()

        return oltService.getStockTrade(stockTradeRequest)
    }

}