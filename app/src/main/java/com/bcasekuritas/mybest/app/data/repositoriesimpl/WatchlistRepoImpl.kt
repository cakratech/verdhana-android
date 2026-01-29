package com.bcasekuritas.mybest.app.data.repositoriesimpl

//import com.bcasekuritas.rabbitmq.proto.bcas.GetWatchListResponse
import com.bcasekuritas.mybest.app.domain.datasource.remote.WatchlistDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.AllUserWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.UserWatchListRequest
import com.bcasekuritas.mybest.app.domain.repositories.WatchlistRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
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
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WatchlistRepoImpl @Inject constructor(
    private val remoteSource: WatchlistDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : WatchlistRepo {
//    override suspend fun getWatchList(watchListRequest: WatchListRequest): Flow<Resource<GetWatchListResponse?>> = flow {
//        emit(Resource.Success(data = remoteSource.getStockWatchList(watchListRequest), DataSource.REMOTE))
//    }

    override suspend fun getStockDetail(stockDetailRequest: StockWatchListRequest): Flow<Resource<CurrentMessageResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getStockDetail(stockDetailRequest), DataSource.REMOTE))
    }

    override suspend fun getUserWatchList(userWatchListRequest: UserWatchListRequest): Flow<Resource<SimpleUserWatchListResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getUserWatchList(userWatchListRequest), DataSource.REMOTE))
    }

    override suspend fun getAllUserWatchlist(allUserWatchListRequest: AllUserWatchListRequest): Flow<Resource<SimpleAllUserWatchListResponse?>> = flow{
        emit(Resource.Success(data = remoteSource.getAllUserWatchlist(allUserWatchListRequest), DataSource.REMOTE))
    }

    override suspend fun addUserWatchList(userWatchListRequest: UserWatchListRequest): Flow<Resource<AddUserWatchListGroupResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.addUserWatchList(userWatchListRequest), DataSource.REMOTE))
    }

    override suspend fun addItemCategory(userWatchListRequest: UserWatchListRequest): Flow<Resource<AddUserWatchListItemResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.addItemCategory(userWatchListRequest), DataSource.REMOTE))
    }

    override suspend fun removeWatchListCategory(userWatchListRequest: UserWatchListRequest): Flow<Resource<RemoveUserWatchListGroupResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.removeWatchListCategory(userWatchListRequest), DataSource.REMOTE))
    }

    override suspend fun removeItemCategory(userWatchListRequest: UserWatchListRequest): Flow<Resource<RemoveUserWatchListItemResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.removeItemCategory(userWatchListRequest), DataSource.REMOTE))
    }

    //    override suspend fun startWatchList(): Flow<Resource<TradeSummary?>> = flow {
//        val miListener =
//            MQMessageListener<MIMessage> { event ->
//                if (event?.protoMsg?.type == MIType.TRADE_SUMMARY) {
//                    val tradeSummaryProto = event.protoMsg.tradeSummary
//                }
//            }
//        remoteSource.startWatchList(miListener)
//    }

    override suspend fun startTradeSum() {
        remoteSource.startTradeSum()
    }

    override suspend fun subscribeAllTradeSum(routingKeys: List<String>) {
        remoteSource.subscribeAllTradeSum(routingKeys)
    }

    override suspend fun subscribeTradeSum(routingKey: String) {
        remoteSource.subscribeTradeSum(routingKey)
    }

    override suspend fun setListenerTradeSum(miListener: MQMessageListener<MIMessage>?) {
        remoteSource.setListenerTradeSum(miListener)
    }

    override suspend fun unSubscribeAllTradeSum(routingKeys: List<String>) {
        remoteSource.unSubscribeAllTradeSum(routingKeys)
    }

    override suspend fun unSubscribeTradeSum(routingKey: String) {
        remoteSource.unSubscribeTradeSum(routingKey)
    }

    override suspend fun stopTradeSum() {
        remoteSource.stopTradeSum()
    }

}