package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.domain.dto.request.StockRankInfoRequest
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.datafeed.StockRankingResponse
import kotlinx.coroutines.flow.Flow

interface CategoryRepo {

    suspend fun getStockRankInfo(stockRankInfoRequest: StockRankInfoRequest): Flow<Resource<StockRankingResponse?>>
}