package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.domain.dto.request.IndexSectorDataRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IndexSectorRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockIndexSectorRequest
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageResponse
import com.bcasekuritas.rabbitmq.proto.news.StockIndexMappingByStockIndexResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewIndexSectorResponse

interface SectorDataSource {

    suspend fun getIndexSector(indexSectorRequest: IndexSectorRequest): ViewIndexSectorResponse?

    suspend fun getIndexSectorDetailData(indexSectorDataRequest: IndexSectorDataRequest): CurrentMessageResponse?

    suspend fun getStockIndexSector(stockIndexSectorRequest: StockIndexSectorRequest): StockIndexMappingByStockIndexResponse?
}