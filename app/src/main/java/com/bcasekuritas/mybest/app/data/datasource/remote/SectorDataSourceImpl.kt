package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.SectorDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.IndexSectorDataRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IndexSectorRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockIndexSectorRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockWatchListRequest
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageRequest
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.MIType
import com.bcasekuritas.rabbitmq.proto.news.StockIndexMappingByStockIndexRequest
import com.bcasekuritas.rabbitmq.proto.news.StockIndexMappingByStockIndexResponse
import com.bcasekuritas.rabbitmq.proto.news.ViewIndexSectorRequest
import com.bcasekuritas.rabbitmq.proto.news.ViewIndexSectorResponse
import javax.inject.Inject

class SectorDataSourceImpl @Inject constructor(
    private val oltService: OLTService
) : SectorDataSource {

    override suspend fun getIndexSector(indexSectorRequest: IndexSectorRequest): ViewIndexSectorResponse? {
        val indexSectorReq = ViewIndexSectorRequest.newBuilder()
            .setUserId(indexSectorRequest.userId)
            .setSessionId(indexSectorRequest.sessionId)
            .build()

        return oltService.getIndexSector(indexSectorReq)
    }

    override suspend fun getIndexSectorDetailData(indexSectorDataRequest: IndexSectorDataRequest): CurrentMessageResponse? {
        val indexSectorReq = CurrentMessageRequest.newBuilder()
            .setUserId(indexSectorDataRequest.userId)
            .setSessionId(indexSectorDataRequest.sessionId)
            .setBoardCode(indexSectorDataRequest.board)
            .setDataType(MIType.INDICE_SUMMARY)
            .addAllItemCode(indexSectorDataRequest.listItemCode)
            .build()

        return oltService.getIndiceSummaryCurrentMessage(indexSectorReq)
    }

    override suspend fun getStockIndexSector(stockIndexSectorRequest: StockIndexSectorRequest): StockIndexMappingByStockIndexResponse? {
        val stockIndexSectorReq = StockIndexMappingByStockIndexRequest.newBuilder().apply {
            setUserId(stockIndexSectorRequest.userId)
            setSessionId(stockIndexSectorRequest.sessionId)
            setStockIndexId(stockIndexSectorRequest.indexSectorId)
            if (stockIndexSectorRequest.isPaging) {
                setSize(stockIndexSectorRequest.size)
                setPage(stockIndexSectorRequest.page)
            }
        }.build()

        return oltService.getStockIndexSector(stockIndexSectorReq)
    }
}