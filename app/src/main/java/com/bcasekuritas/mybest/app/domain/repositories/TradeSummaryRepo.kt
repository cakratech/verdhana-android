package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.domain.dto.request.TradeSumRequest
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.chart.Cf.CFMessage.TradeSummaryResponse
import kotlinx.coroutines.flow.Flow

interface TradeSummaryRepo {
    suspend fun getTradeSummary(tradeSumRequest: TradeSumRequest?): Flow<Resource<TradeSummaryResponse?>>

}