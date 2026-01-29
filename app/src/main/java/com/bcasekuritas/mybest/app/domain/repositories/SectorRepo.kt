package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.domain.dto.request.IndexSectorDataRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IndexSectorRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IndiceDataRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockIndexSectorRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockWatchListRequest
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageResponse
import com.bcasekuritas.rabbitmq.proto.news.StockIndexMappingByStockIndexResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewIndexSectorResponse
import kotlinx.coroutines.flow.Flow

interface SectorRepo {

    suspend fun getIndexSector(indexSectorRequest: IndexSectorRequest): Flow<Resource<ViewIndexSectorResponse?>>

    suspend fun getIndexSectorDetailData(indexSectorDataRequest: IndexSectorDataRequest): Flow<Resource<CurrentMessageResponse?>>

    suspend fun getStockIndexSector(stockIndexSectorRequest: StockIndexSectorRequest): Flow<Resource<StockIndexMappingByStockIndexResponse?>>
}