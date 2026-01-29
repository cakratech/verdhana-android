package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.domain.dto.request.BrokerRankActivityReq
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerRankByStockReq
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerSummaryRankingReq
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerStockSumRequest
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerListResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityDiscoverRequest
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockNetDiscoverRequest
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockNetDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankingDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerStockSummaryResponse

interface BrokerSumDataSource {
    suspend fun getBrokerStockSummary(brokerStockSummaryRequest: BrokerStockSumRequest): BrokerStockSummaryResponse?
    suspend fun getBrokerRankByStock(brokerRankByStockReq: BrokerRankByStockReq): BrokerRankByStockDiscoverResponse?
    suspend fun getBrokerList(userId: String): BrokerListResponse?
    suspend fun getBrokerRankActivity(brokerRankActivityReq: BrokerRankActivityReq): BrokerRankActivityDiscoverResponse?
    suspend fun getBrokeRankRanking(brokerRankRankingReq: BrokerSummaryRankingReq): BrokerRankingDiscoverResponse?
    suspend fun getBrokerSummaryByStockNet(brokerRankByStockReq: BrokerRankByStockReq): BrokerRankByStockNetDiscoverResponse?
}