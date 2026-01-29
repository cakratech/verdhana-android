package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.AnalysisDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.ChartIntradayRequest
import com.bcasekuritas.mybest.app.domain.dto.request.FibonacciPivotPointReq
import com.bcasekuritas.mybest.app.domain.dto.request.GetPbvBandReq
import com.bcasekuritas.mybest.app.domain.dto.request.GetPerBandReq
import com.bcasekuritas.mybest.app.domain.dto.request.StockAnalysisRatingReq
import com.bcasekuritas.mybest.app.domain.repositories.AnalysisRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import com.bcasekuritas.rabbitmq.proto.chart.Cf
import com.bcasekuritas.rabbitmq.proto.news.FibonacciPivotPointResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPbvBandResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPbvDataResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPerBandResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPerDataResponse
import com.bcasekuritas.rabbitmq.proto.news.StockAnalysisRatingResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AnalysisRepoImpl @Inject constructor(
    private val remoteSource: AnalysisDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
): AnalysisRepo {
    override suspend fun getPerBand(getPerBandReq: GetPerBandReq): Flow<Resource<GetPerBandResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getPerBand(getPerBandReq), DataSource.REMOTE))
    }

    override suspend fun getPerData(getPerBandReq: GetPerBandReq): Flow<Resource<GetPerDataResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getPerData(getPerBandReq), DataSource.REMOTE))
    }

    override suspend fun getPbvBand(getPbvBandReq: GetPbvBandReq): Flow<Resource<GetPbvBandResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getPbvBand(getPbvBandReq), DataSource.REMOTE))
    }

    override suspend fun getPbvData(getPbvBandReq: GetPbvBandReq): Flow<Resource<GetPbvDataResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getPbvData(getPbvBandReq), DataSource.REMOTE))
    }

    override suspend fun getStockAnalysisRating(stockAnalysisRatingReq: StockAnalysisRatingReq): Flow<Resource<StockAnalysisRatingResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getStockAnalysisRating(stockAnalysisRatingReq), DataSource.REMOTE))
    }

    override suspend fun getFibonacciPivotPoint(fibonacciPivotPointReq: FibonacciPivotPointReq): Flow<Resource<FibonacciPivotPointResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getFibonacciPivotPoint(fibonacciPivotPointReq), DataSource.REMOTE))
    }

    override suspend fun getChartIntradayPrice(chartIntradayRequest: ChartIntradayRequest): Flow<Resource<Cf.CFMessage.IntradayPriceResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getChartIntradayPrice(chartIntradayRequest), DataSource.REMOTE))
    }
}