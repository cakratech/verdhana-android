package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.BrokerSumDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerRankActivityReq
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerRankByStockReq
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerSummaryRankingReq
import com.bcasekuritas.mybest.app.domain.dto.request.BrokerStockSumRequest
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerListRequest
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerListResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityDiscoverRequest
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankActivityDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockDiscoverRequest
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockNetDiscoverRequest
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankByStockNetDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankingDiscoverRequest
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerRankingDiscoverResponse
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerStockSummaryRequest
import com.bcasekuritas.rabbitmq.proto.brokerrank.BrokerStockSummaryResponse
import com.bcasekuritas.rabbitmq.proto.news.GetPerBandRequest
import javax.inject.Inject

class BrokerSumDataSourceImpl @Inject constructor(
    private val oltService: OLTService
) : BrokerSumDataSource {
    override suspend fun getBrokerStockSummary(brokerStockSumRequest: BrokerStockSumRequest): BrokerStockSummaryResponse? {
        val brokerStockSummaryRequest = BrokerStockSummaryRequest
            .newBuilder()
            .setUserId(brokerStockSumRequest.userId)
            .setTrxDate(brokerStockSumRequest.trxDate)
            .setStockCode(brokerStockSumRequest.stockCode)
            .build()

        return oltService.getBrokerStockSummary(brokerStockSummaryRequest)    }

    override suspend fun getBrokerRankByStock(brokerRankByStockReq: BrokerRankByStockReq): BrokerRankByStockDiscoverResponse? {
        val brokerRankByStockDiscoverRequest = BrokerRankByStockDiscoverRequest
            .newBuilder()
            .setUserId(brokerRankByStockReq.userId)
            .setStartDate(brokerRankByStockReq.startDate)
            .setEndDate(brokerRankByStockReq.endDate)
            .setBoardCode(brokerRankByStockReq.boardCode)
            .setStockCode(brokerRankByStockReq.stockCode)
            .setBrokerType(brokerRankByStockReq.brokerType)
            .setSessionId(brokerRankByStockReq.sessionId)
            .build()

        return oltService.getBrokerRankByStock(brokerRankByStockDiscoverRequest)
    }

    override suspend fun getBrokerSummaryByStockNet(brokerRankByStockReq: BrokerRankByStockReq): BrokerRankByStockNetDiscoverResponse? {
        val brokerRankByStockNetRequest = BrokerRankByStockNetDiscoverRequest.newBuilder()
            .setUserId(brokerRankByStockReq.userId)
            .setStartDate(brokerRankByStockReq.startDate)
            .setEndDate(brokerRankByStockReq.endDate)
            .setBoardCode(brokerRankByStockReq.boardCode)
            .setStockCode(brokerRankByStockReq.stockCode)
            .setBrokerType(brokerRankByStockReq.brokerType)
            .setSessionId(brokerRankByStockReq.sessionId)
            .build()

        return oltService.getBrokerSummaryByStockNet(brokerRankByStockNetRequest)
    }

    override suspend fun getBrokerList(userId: String): BrokerListResponse? {
        val brokerListRequest = BrokerListRequest.newBuilder()
            .setUserId(userId)
            .build()

        return oltService.getBrokerList(brokerListRequest)
    }

    override suspend fun getBrokerRankActivity(brokerRankActivityReq: BrokerRankActivityReq): BrokerRankActivityDiscoverResponse? {
        val brokerRankActivityRequest = BrokerRankActivityDiscoverRequest.newBuilder()
            .setUserId(brokerRankActivityReq.userId)
            .setBrokerCode(brokerRankActivityReq.brokerCode)
            .setStartDate(brokerRankActivityReq.startDate)
            .setEndDate(brokerRankActivityReq.endDate)
            .setSessionId(brokerRankActivityReq.sessionId)
            .setBoardCode(brokerRankActivityReq.boardCode)
            .build()

        return oltService.getBrokerRankActivity(brokerRankActivityRequest)
    }

    override suspend fun getBrokeRankRanking(brokerRankRankingReq: BrokerSummaryRankingReq): BrokerRankingDiscoverResponse? {
        val brokerRankingDiscoverRequest = BrokerRankingDiscoverRequest.newBuilder()
            .setUserId(brokerRankRankingReq.userId)
            .setStartDate(brokerRankRankingReq.startDate)
            .setEndDate(brokerRankRankingReq.endDate)
            .setSessionId(brokerRankRankingReq.sessionId)
            .setSortType(brokerRankRankingReq.sortType)
            .build()

        return oltService.getBrokerRankRanking(brokerRankingDiscoverRequest)
    }
}