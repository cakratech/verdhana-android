package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.TradeBookDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.TradeBookRequest
import com.bcasekuritas.mybest.app.domain.repositories.TradeBookRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeBookResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.TradeBookTimeResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TradeBookRepoImpl @Inject constructor(
    private val remoteServices: TradeBookDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : TradeBookRepo{
    override suspend fun getTradeBook(tradeBookRequest: TradeBookRequest?): Flow<Resource<TradeBookResponse?>> = flow{
        emit(Resource.Success(data = remoteServices.getTradeBook(tradeBookRequest), DataSource.REMOTE))
    }

    override suspend fun getTradeBookTime(tradeBookTimeRequest: TradeBookRequest?): Flow<Resource<TradeBookTimeResponse?>> = flow{
        emit(Resource.Success(data = remoteServices.getTradeBookTime(tradeBookTimeRequest), DataSource.REMOTE))
    }
}