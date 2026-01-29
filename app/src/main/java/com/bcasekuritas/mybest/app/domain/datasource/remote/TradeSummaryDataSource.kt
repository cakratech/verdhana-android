package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.domain.dto.request.TradeSumRequest
import com.bcasekuritas.rabbitmq.proto.chart.Cf
import com.bcasekuritas.rabbitmq.proto.chart.Cf.CFMessage.TradeSummaryResponse

interface TradeSummaryDataSource {
    suspend fun getTradeSummary(tradeSummaryRequest: TradeSumRequest?): TradeSummaryResponse?

}