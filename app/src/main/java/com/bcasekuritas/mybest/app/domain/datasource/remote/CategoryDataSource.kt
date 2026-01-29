package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.domain.dto.request.StockRankInfoRequest
import com.bcasekuritas.rabbitmq.proto.datafeed.StockRankingResponse

interface CategoryDataSource {

    suspend fun getStockRankInfo(stockRankInfoRequest: StockRankInfoRequest): StockRankingResponse?

}