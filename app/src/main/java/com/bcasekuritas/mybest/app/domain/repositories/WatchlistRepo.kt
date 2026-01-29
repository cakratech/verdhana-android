package com.bcasekuritas.mybest.app.domain.repositories

//import com.bcasekuritas.rabbitmq.proto.bcas.GetWatchListResponse
import com.bcasekuritas.mybest.app.domain.dto.request.AllUserWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.UserWatchListRequest
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
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
import kotlinx.coroutines.flow.Flow

interface WatchlistRepo {

//    suspend fun getWatchList(watchListRequest: WatchListRequest): Flow<Resource<GetWatchListResponse?>>

    suspend fun getStockDetail(stockDetailRequest: StockWatchListRequest): Flow<Resource<CurrentMessageResponse?>>
    suspend fun getUserWatchList(userWatchListRequest: UserWatchListRequest): Flow<Resource<SimpleUserWatchListResponse?>>
    suspend fun getAllUserWatchlist(allUserWatchListRequest: AllUserWatchListRequest): Flow<Resource<SimpleAllUserWatchListResponse?>>
    suspend fun addUserWatchList(userWatchListRequest: UserWatchListRequest): Flow<Resource<AddUserWatchListGroupResponse?>>

    suspend fun addItemCategory(userWatchListRequest: UserWatchListRequest): Flow<Resource<AddUserWatchListItemResponse?>>
    suspend fun removeWatchListCategory(userWatchListRequest: UserWatchListRequest): Flow<Resource<RemoveUserWatchListGroupResponse?>>
    suspend fun removeItemCategory(userWatchListRequest: UserWatchListRequest): Flow<Resource<RemoveUserWatchListItemResponse?>>

    suspend fun startTradeSum()

    suspend fun setListenerTradeSum(miListener: MQMessageListener<MIMessage>?)

    suspend fun subscribeAllTradeSum(routingKeys: List<String>)

    suspend fun subscribeTradeSum(routingKey: String)

    suspend fun unSubscribeAllTradeSum(routingKey: List<String>)

    suspend fun unSubscribeTradeSum(routingKey: String)

    suspend fun stopTradeSum()

}