package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.SectorDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.IndexSectorDataRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IndexSectorRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockIndexSectorRequest
import com.bcasekuritas.mybest.app.domain.repositories.SectorRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageResponse
import com.bcasekuritas.rabbitmq.proto.news.StockIndexMappingByStockIndexResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewIndexSectorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SectorRepoImpl @Inject constructor(
    private val remoteSource: SectorDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : SectorRepo {

    override suspend fun getIndexSector(indexSectorRequest: IndexSectorRequest): Flow<Resource<ViewIndexSectorResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getIndexSector(indexSectorRequest), DataSource.REMOTE))
    }

    override suspend fun getIndexSectorDetailData(indexSectorDataRequest: IndexSectorDataRequest): Flow<Resource<CurrentMessageResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getIndexSectorDetailData(indexSectorDataRequest), DataSource.REMOTE))
    }

    override suspend fun getStockIndexSector(stockIndexSectorRequest: StockIndexSectorRequest): Flow<Resource<StockIndexMappingByStockIndexResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getStockIndexSector(stockIndexSectorRequest), DataSource.REMOTE))
    }
}