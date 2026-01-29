package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.StockTradeDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.StockTradeReq
import com.bcasekuritas.mybest.app.domain.repositories.StockTradeRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import com.bcasekuritas.rabbitmq.proto.datafeed.StockTradeResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StockTradeRepoImpl @Inject constructor(
    private val stockTradeDataSource: StockTradeDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : StockTradeRepo {
    override suspend fun getStockTrade(stockTradeRequest: StockTradeReq?): Flow<Resource<StockTradeResponse?>> = flow{
        emit(Resource.Success(data = stockTradeDataSource.getStockTrade(stockTradeRequest), DataSource.REMOTE))
    }

}