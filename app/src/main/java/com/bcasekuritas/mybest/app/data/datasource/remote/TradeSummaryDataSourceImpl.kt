package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.TradeSummaryDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.TradeSumRequest
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.chart.Cf
import com.bcasekuritas.rabbitmq.proto.chart.Cf.CFMessage.TradeSummaryRequest
import com.bcasekuritas.rabbitmq.proto.chart.Cf.CFMessage.TradeSummaryResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.StockTradeRequest
import javax.inject.Inject

class TradeSummaryDataSourceImpl  @Inject constructor(
    private val oltService: OLTService
) : TradeSummaryDataSource {
    override suspend fun getTradeSummary(tradeSumRequest: TradeSumRequest?): TradeSummaryResponse? {
        val tradeSummaryRequest = TradeSummaryRequest
            .newBuilder()
            .setUserId(tradeSumRequest?.userId)
            .setSessionId(tradeSumRequest?.sessionId)
            .setSecCode(tradeSumRequest?.secCode)
            .setStartDate(tradeSumRequest!!.startDate)
            .setEndDate(tradeSumRequest.endDate)
            .build()

        return oltService.getTradeSummary(tradeSummaryRequest)
    }
}