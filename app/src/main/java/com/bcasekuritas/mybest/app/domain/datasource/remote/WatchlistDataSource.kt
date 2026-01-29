package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.domain.dto.request.AllUserWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.UserWatchListRequest
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.bcas.AddUserWatchListGroupResponse
import com.bcasekuritas.rabbitmq.proto.bcas.AddUserWatchListItemResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RemoveUserWatchListGroupResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RemoveUserWatchListItemResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleAllUserWatchListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleUserWatchListResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.google.protobuf.GeneratedMessage

interface WatchlistDataSource {

    suspend fun getUserWatchList(userWatchListRequest: UserWatchListRequest): SimpleUserWatchListResponse?
    suspend fun getAllUserWatchlist(userWatchlistRequest: AllUserWatchListRequest): SimpleAllUserWatchListResponse?
    suspend fun addUserWatchList(userWatchListRequest: UserWatchListRequest): AddUserWatchListGroupResponse?
    suspend fun addItemCategory(userWatchListRequest: UserWatchListRequest): AddUserWatchListItemResponse?
    suspend fun removeWatchListCategory(userWatchListRequest: UserWatchListRequest): RemoveUserWatchListGroupResponse?
    suspend fun removeItemCategory(userWatchListRequest: UserWatchListRequest): RemoveUserWatchListItemResponse?

//    suspend fun getStockWatchList(watchListRequests: WatchListRequest): GetWatchListResponse?

    suspend fun addStockWatchList(addWatchListRequest: StockWatchListRequest): CurrentMessageResponse?

//    suspend fun removeStockWatchList(removeWatchList: StockWatchListRequest): PersistWatchListResponse?

    suspend fun getStockDetail(stockDetail: StockWatchListRequest): CurrentMessageResponse?

    suspend fun startTradeSum()

    suspend fun setListenerTradeSum(miListener: MQMessageListener<MIMessage>?)

    suspend fun subscribeAllTradeSum(routingKeys: List<String>)

    suspend fun subscribeTradeSum(routingKey: String)

    suspend fun unSubscribeAllTradeSum(routingKey: List<String>)

    suspend fun unSubscribeTradeSum(routingKey: String)

    suspend fun stopTradeSum()
}