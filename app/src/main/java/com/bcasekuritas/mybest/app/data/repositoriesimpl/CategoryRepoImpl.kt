package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.CategoryDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.StockRankInfoRequest
import com.bcasekuritas.mybest.app.domain.repositories.CategoryRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import com.bcasekuritas.rabbitmq.proto.datafeed.StockRankingResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CategoryRepoImpl @Inject constructor(
    private val remoteSource: CategoryDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : CategoryRepo {

    override suspend fun getStockRankInfo(stockRankInfoRequest: StockRankInfoRequest): Flow<Resource<StockRankingResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getStockRankInfo(stockRankInfoRequest), DataSource.REMOTE))
    }
}