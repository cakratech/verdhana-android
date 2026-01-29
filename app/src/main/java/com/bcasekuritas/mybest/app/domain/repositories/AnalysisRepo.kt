package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.domain.dto.request.ChartIntradayRequest
import com.bcasekuritas.mybest.app.domain.dto.request.FibonacciPivotPointReq
import com.bcasekuritas.mybest.app.domain.dto.request.GetPbvBandReq
import com.bcasekuritas.mybest.app.domain.dto.request.GetPerBandReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockAnalysisRatingReq
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.chart.Cf.CFMessage.IntradayPriceRequest
import com.bcasekuritas.rabbitmq.proto.chart.Cf.CFMessage.IntradayPriceResponse
import com.bcasekuritas.rabbitmq.proto.news.FibonacciPivotPointResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPbvBandResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPbvDataResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPerBandResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPerDataResponse
import com.bcasekuritas.rabbitmq.proto.news.StockAnalysisRatingResponse
import kotlinx.coroutines.flow.Flow

interface AnalysisRepo {
    suspend fun getPerBand(getPerBandReq: GetPerBandReq): Flow<Resource<GetPerBandResponse?>>
    suspend fun getPerData(getPerBandReq: GetPerBandReq): Flow<Resource<GetPerDataResponse?>>
    suspend fun getPbvBand(getPbvBandReq: GetPbvBandReq): Flow<Resource<GetPbvBandResponse?>>
    suspend fun getPbvData(getPbvBandReq: GetPbvBandReq): Flow<Resource<GetPbvDataResponse?>>
    suspend fun getStockAnalysisRating(stockAnalysisRatingReq: StockAnalysisRatingReq): Flow<Resource<StockAnalysisRatingResponse?>>
    suspend fun getFibonacciPivotPoint(fibonacciPivotPointReq: FibonacciPivotPointReq): Flow<Resource<FibonacciPivotPointResponse?>>
    suspend fun getChartIntradayPrice(chartIntradayRequest: ChartIntradayRequest): Flow<Resource<IntradayPriceResponse?>>
}