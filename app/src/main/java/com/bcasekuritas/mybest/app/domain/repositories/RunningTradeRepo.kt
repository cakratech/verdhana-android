package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.data.entity.FilterRunningTradeObject
import com.bcasekuritas.mybest.app.domain.dto.request.LatestTradeDetailRequest
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.datafeed.LatestTradeDetailResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import kotlinx.coroutines.flow.Flow

interface RunningTradeRepo {

    suspend fun getLatestTradeDetail(latestTradeDetailRequest: LatestTradeDetailRequest): Flow<Resource<LatestTradeDetailResponse?>>

    suspend fun startRunningTrade()

    suspend fun setListenerRunningTrade(miListener: MQMessageListener<MIMessage>?)

    suspend fun subscribeRunningTrade(routingKey: String?)

    suspend fun unsubscribeRunningTrade(stockCode: String?)

    suspend fun stopRunningTrade()

    /** Filter DAO */
    suspend fun setDefaultFilter(filterRunningTrade: FilterRunningTradeObject)
    suspend fun getDefaultFilter(userId: String): Flow<Resource<FilterRunningTradeObject>>
    suspend fun resetDefaultFilter(userId: String)

}