package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.domain.dto.request.StockTradeReq
import com.bcasekuritas.rabbitmq.proto.datafeed.StockTradeResponse

interface StockTradeDataSource {
    suspend fun getStockTrade(stockTradeRequest: StockTradeReq?): StockTradeResponse?
}