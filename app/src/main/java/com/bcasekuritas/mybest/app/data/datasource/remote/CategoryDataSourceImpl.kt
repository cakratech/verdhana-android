package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.CategoryDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.StockRankInfoRequest
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.datafeed.StockRankingRequest
import com.bcasekuritas.rabbitmq.proto.datafeed.StockRankingResponse
import javax.inject.Inject

class CategoryDataSourceImpl @Inject constructor(
    private val oltService: OLTService
) : CategoryDataSource {

    override suspend fun getStockRankInfo(stockRankInfoRequest: StockRankInfoRequest): StockRankingResponse? {
        val stockRankRequest = StockRankingRequest.newBuilder()
            .setUserId(stockRankInfoRequest.userId)
            .setSessionId(stockRankInfoRequest.sessionId)
            .setSortAscending(stockRankInfoRequest.sortAscending)
            .setSortType(stockRankInfoRequest.sortType)
            .setSeqNo(stockRankInfoRequest.seqNo)
            .setMaxData(stockRankInfoRequest.maxData)
            .build()

        return oltService.getStockRankInfo(stockRankRequest)
    }
}