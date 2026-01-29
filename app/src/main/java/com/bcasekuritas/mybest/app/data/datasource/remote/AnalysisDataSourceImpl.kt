package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.AnalysisDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.ChartIntradayRequest
import com.bcasekuritas.mybest.app.domain.dto.request.FibonacciPivotPointReq
import com.bcasekuritas.mybest.app.domain.dto.request.GetPbvBandReq
import com.bcasekuritas.mybest.app.domain.dto.request.GetPerBandReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockAnalysisRatingReq
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.bcas.LogonRequest
import com.bcasekuritas.rabbitmq.proto.chart.Cf
import com.bcasekuritas.rabbitmq.proto.chart.Cf.CFMessage.IntradayPriceRequest
import com.bcasekuritas.rabbitmq.proto.news.FibonacciPivotPointRequest
import com.bcasekuritas.rabbitmq.proto.news.FibonacciPivotPointResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPbvBandRequest
import com.bcasekuritas.rabbitmq.proto.news.GetPbvBandResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPbvDataRequest
import com.bcasekuritas.rabbitmq.proto.news.GetPbvDataResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPerBandRequest
import com.bcasekuritas.rabbitmq.proto.news.GetPerBandResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPerDataRequest
import com.bcasekuritas.rabbitmq.proto.news.GetPerDataResponse
import com.bcasekuritas.rabbitmq.proto.news.StockAnalysisRatingRequest
import com.bcasekuritas.rabbitmq.proto.news.StockAnalysisRatingResponse
import javax.inject.Inject

class AnalysisDataSourceImpl @Inject constructor(
    private val oltService: OLTService
): AnalysisDataSource {
    override suspend fun getPerBand(getPerBandReq: GetPerBandReq): GetPerBandResponse? {
        val getPerBandRequest = GetPerBandRequest
            .newBuilder()
            .setUserId(getPerBandReq.userId)
            .setSessionId(getPerBandReq.sessionId)
            .setStockCode(getPerBandReq.stockCode)
            .setPeriod(getPerBandReq.period)
            .build()

        return oltService.getPerBand(getPerBandRequest)
    }

    override suspend fun getPerData(getPerBandReq: GetPerBandReq): GetPerDataResponse? {
        val getPerDataRequest = GetPerDataRequest
            .newBuilder()
            .setUserId(getPerBandReq.userId)
            .setSessionId(getPerBandReq.sessionId)
            .setStockCode(getPerBandReq.stockCode)
            .setPeriod(getPerBandReq.period)
            .build()

        return oltService.getPerData(getPerDataRequest)
    }

    override suspend fun getPbvBand(getPbvBandReq: GetPbvBandReq): GetPbvBandResponse? {
        val getPbvBandRequest = GetPbvBandRequest
            .newBuilder()
            .setUserId(getPbvBandReq.userId)
            .setSessionId(getPbvBandReq.sessionId)
            .setStockCode(getPbvBandReq.stockCode)
            .setPeriod(getPbvBandReq.period)
            .build()

        return oltService.getPbvBand(getPbvBandRequest)
    }

    override suspend fun getPbvData(getPbvBandReq: GetPbvBandReq): GetPbvDataResponse? {
        val getPbvDataRequest = GetPbvDataRequest
            .newBuilder()
            .setUserId(getPbvBandReq.userId)
            .setSessionId(getPbvBandReq.sessionId)
            .setStockCode(getPbvBandReq.stockCode)
            .setPeriod(getPbvBandReq.period)
            .build()

        return oltService.getPbvData(getPbvDataRequest)
    }

    override suspend fun getStockAnalysisRating(stockAnalysisRatingReq: StockAnalysisRatingReq): StockAnalysisRatingResponse? {
        val stockAnalysisRatingRequest = StockAnalysisRatingRequest
            .newBuilder()
            .setUserId(stockAnalysisRatingReq.userId)
            .setSessionId(stockAnalysisRatingReq.sessionId)
            .setStockCode(stockAnalysisRatingReq.stockCode)
            .build()

        return oltService.getStockAnalysisRating(stockAnalysisRatingRequest)
    }

    override suspend fun getFibonacciPivotPoint(fibonacciPivotPointReq: FibonacciPivotPointReq): FibonacciPivotPointResponse? {
        val fibonacciPivotPointRequest = FibonacciPivotPointRequest
            .newBuilder()
            .setUserId(fibonacciPivotPointReq.userId)
            .setSessionId(fibonacciPivotPointReq.sessionId)
            .setStockCode(fibonacciPivotPointReq.stockCode)
            .build()

        return oltService.getFibonacciPivotPoint(fibonacciPivotPointRequest)
    }

    override suspend fun getChartIntradayPrice(chartIntradayRequest: ChartIntradayRequest): Cf.CFMessage.IntradayPriceResponse? {
        val intradayPriceRequest = IntradayPriceRequest.newBuilder().apply {
            setUserId(chartIntradayRequest.userId)
            setSessionId(chartIntradayRequest.sessionId)
            setItemCode(chartIntradayRequest.itemCode)
            if (!chartIntradayRequest.boardCode.equals("")) {
                setBoardCode(chartIntradayRequest.boardCode)
            }
            setTimeUnit(chartIntradayRequest.timeUnit)
            if (chartIntradayRequest.timeUnit == 0){
                setTrDate(chartIntradayRequest.ssDateTo)
            } else {
                setSsDateFrom(chartIntradayRequest.ssDateFrom)
                setSsDateTo(chartIntradayRequest.ssDateTo)
            }
        }.build()

        return oltService.getChartIntradayPrice(intradayPriceRequest)
    }
}