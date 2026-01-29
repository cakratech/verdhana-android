package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.domain.dto.request.TradeBookRequest
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeBookResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeBookTimeResponse
import kotlinx.coroutines.flow.Flow

interface TradeBookRepo {
    suspend fun getTradeBook(tradeBookRequest: TradeBookRequest?): Flow<Resource<TradeBookResponse?>>
    suspend fun getTradeBookTime(tradeBookTimeRequest: TradeBookRequest?): Flow<Resource<TradeBookTimeResponse?>>


}