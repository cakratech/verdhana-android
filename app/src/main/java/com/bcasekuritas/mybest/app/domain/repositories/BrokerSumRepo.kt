package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.domain.dto.request.BrokerRankActivityReq
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerRankByStockReq
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerSummaryRankingReq
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerStockSumRequest
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerListResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockNetDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankingDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerStockSummaryResponse
import kotlinx.coroutines.flow.Flow

interface BrokerSumRepo {
    suspend fun getBrokerStockSummary(brokerStockSummaryRequest: BrokerStockSumRequest): Flow<Resource<BrokerStockSummaryResponse?>>
    suspend fun getBrokerRankByStock(brokerRankByStockReq: BrokerRankByStockReq): Flow<Resource<BrokerRankByStockDiscoverResponse?>>
    suspend fun getBrokerList(userId: String): Flow<Resource<BrokerListResponse?>>
    suspend fun getBrokerRankActivity(brokerRankActivityReq: BrokerRankActivityReq): Flow<Resource<BrokerRankActivityDiscoverResponse?>>
    suspend fun getBrokerRankRanking(brokerRankRankingReq: BrokerSummaryRankingReq): Flow<Resource<BrokerRankingDiscoverResponse?>>
    suspend fun getBrokerSummaryByStockNet(brokerRankByStockReq: BrokerRankByStockReq): Flow<Resource<BrokerRankByStockNetDiscoverResponse?>>
}