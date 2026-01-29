package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.data.entity.FilterRunningTradeObject
import com.bcasekuritas.mybest.app.domain.dto.request.LatestTradeDetailRequest
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.datafeed.LatestTradeDetailResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage

interface RunningTradeDataSource {

    suspend fun getLatestTradeDetail(latestTradeDetailRequest: LatestTradeDetailRequest) : LatestTradeDetailResponse?

    suspend fun startRunningTrade()

    suspend fun setListenerRunningTrade(miListener: MQMessageListener<MIMessage>?)

    suspend fun subscribeRunningTrade(routingKey: String?)

    suspend fun unsubscribeRunningTrade(stockCode: String?)

    suspend fun stopRunningTrade()


    /** Filter DAO */
    suspend fun setDefaultFilter(filterRunningTrade: FilterRunningTradeObject)
    suspend fun getDefaultFilter(userId: String): FilterRunningTradeObject
    suspend fun resetDefaultFilter(userId: String)
}