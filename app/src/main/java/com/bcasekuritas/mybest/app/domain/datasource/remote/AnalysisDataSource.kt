package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.domain.dto.request.ChartIntradayRequest
import com.bcasekuritas.mybest.app.domain.dto.request.FibonacciPivotPointReq
import com.bcasekuritas.mybest.app.domain.dto.request.GetPbvBandReq
import com.bcasekuritas.mybest.app.domain.dto.request.GetPerBandReq
import com.bcasekuritas.mybest.app.domain.dto.request.LogonRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockAnalysisRatingReq
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.LogonResponse
import com.bcasekuritas.rabbitmq.proto.chart.Cf.CFMessage.IntradayPriceResponse
import com.bcasekuritas.rabbitmq.proto.news.FibonacciPivotPointResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPbvBandResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPbvDataResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPerBandResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPerDataResponse
import com.bcasekuritas.rabbitmq.proto.news.StockAnalysisRatingResponse
import kotlinx.coroutines.flow.Flow

interface AnalysisDataSource {
    suspend fun getPerBand(getPerBandReq: GetPerBandReq): GetPerBandResponse?
    suspend fun getPerData(getPerBandReq: GetPerBandReq): GetPerDataResponse?
    suspend fun getPbvBand(getPbvBandReq: GetPbvBandReq): GetPbvBandResponse?
    suspend fun getPbvData(getPbvBandReq: GetPbvBandReq): GetPbvDataResponse?
    suspend fun getStockAnalysisRating(stockAnalysisRatingReq: StockAnalysisRatingReq): StockAnalysisRatingResponse?
    suspend fun getFibonacciPivotPoint(fibonacciPivotPointReq: FibonacciPivotPointReq): FibonacciPivotPointResponse?
    suspend fun getChartIntradayPrice(chartIntradayRequest: ChartIntradayRequest): IntradayPriceResponse?
}