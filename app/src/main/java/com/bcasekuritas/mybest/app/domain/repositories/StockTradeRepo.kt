package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.domain.dto.request.StockTradeReq
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.datafeed.StockTradeResponse
import kotlinx.coroutines.flow.Flow

interface StockTradeRepo {
    suspend fun getStockTrade(stockTradeRequest: StockTradeReq?): Flow<Resource<StockTradeResponse?>>

}