package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.data.dao.FilterDao
import com.bcasekuritas.mybest.app.data.entity.FilterRunningTradeObject
import com.bcasekuritas.mybest.app.domain.datasource.remote.RunningTradeDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.LatestTradeDetailRequest
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.datafeed.LatestTradeDetailResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import javax.inject.Inject

class RunningTradeDataSourceImpl @Inject constructor(
    private val oltService: OLTService,
    private val localDataSource: FilterDao
): RunningTradeDataSource{

    override suspend fun getLatestTradeDetail(latestTradeDetailRequest: LatestTradeDetailRequest): LatestTradeDetailResponse? {
        val latestTradeDetailReq = com.bcasekuritas.rabbitmq.proto.datafeed.LatestTradeDetailRequest
            .newBuilder()
            .setUserId(latestTradeDetailRequest.userId)
            .setSessionId(latestTradeDetailRequest.sessionId)
            .build()

        return oltService.getLatestTradeDetail(latestTradeDetailReq)
    }

    override suspend fun startRunningTrade() {
        oltService.startRunningTrade()
    }

    override suspend fun setListenerRunningTrade(miListener: MQMessageListener<MIMessage>?) {
        oltService.setListenerRunningTrade(miListener)
    }

    override suspend fun subscribeRunningTrade(routingKey: String?) {
        oltService.subscribeRunningTrade(routingKey)
    }

    override suspend fun unsubscribeRunningTrade(stockCode: String?) {
        oltService.unsubscribeRunningTrade(stockCode)
    }

    override suspend fun stopRunningTrade() {
        oltService.stopRunningTrade()
    }

    override suspend fun setDefaultFilter(filterRunningTrade: FilterRunningTradeObject) {
        filterRunningTrade.let { filter ->
            localDataSource.insertFilter(
                FilterRunningTradeObject(
                    userId = filter.userId,
                    indexSectorId = filter.indexSectorId,
                    category = filter.category,
                    minPrice = filter.minPrice,
                    maxPrice = filter.maxPrice,
                    minChange = filter.minChange,
                    maxChange = filter.maxChange,
                    minVolume = filter.minVolume,
                    maxVolume = filter.maxVolume,
                    stockCodes = filter.stockCodes
                )
            )
        }
    }

    override suspend fun getDefaultFilter(userId: String): FilterRunningTradeObject {
        return localDataSource.getDefaultFilter(userId)
    }

    override suspend fun resetDefaultFilter(userId: String) {
        localDataSource.deleteFilter(userId)
    }
}