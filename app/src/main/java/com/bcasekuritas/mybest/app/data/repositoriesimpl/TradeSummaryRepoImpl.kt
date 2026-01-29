package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.StockDetailDataSource
import com.bcasekuritas.mybest.app.domain.datasource.remote.TradeSummaryDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.TradeSumRequest
import com.bcasekuritas.mybest.app.domain.repositories.TradeSummaryRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import com.bcasekuritas.rabbitmq.proto.chart.Cf.CFMessage.TradeSummaryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TradeSummaryRepoImpl  @Inject constructor(
    private val tradeSummaryDataSource: TradeSummaryDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : TradeSummaryRepo {
    override suspend fun getTradeSummary(tradeSumRequest: TradeSumRequest?): Flow<Resource<TradeSummaryResponse?>> = flow{
        emit(Resource.Success(data = tradeSummaryDataSource.getTradeSummary(tradeSumRequest), DataSource.REMOTE))
    }
}