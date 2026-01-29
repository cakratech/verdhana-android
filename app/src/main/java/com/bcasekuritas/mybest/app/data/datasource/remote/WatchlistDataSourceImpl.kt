package com.bcasekuritas.mybest.app.data.datasource.remote

//import com.bcasekuritas.rabbitmq.proto.bcas.ExchangeKey
//import com.bcasekuritas.rabbitmq.proto.bcas.GetWatchListRequest
//import com.bcasekuritas.rabbitmq.proto.bcas.GetWatchListResponse
//import com.bcasekuritas.rabbitmq.proto.bcas.PersistWatchListRequest
//import com.bcasekuritas.rabbitmq.proto.bcas.PersistWatchListResponse
import com.bcasekuritas.mybest.app.domain.datasource.remote.WatchlistDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.AllUserWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.StockWatchListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.UserWatchListRequest
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.bcas.AddUserWatchListGroupRequest
import com.bcasekuritas.rabbitmq.proto.bcas.AddUserWatchListGroupResponse
import com.bcasekuritas.rabbitmq.proto.bcas.AddUserWatchListItemRequest
import com.bcasekuritas.rabbitmq.proto.bcas.AddUserWatchListItemResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RemoveUserWatchListGroupRequest
import com.bcasekuritas.rabbitmq.proto.bcas.RemoveUserWatchListGroupResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RemoveUserWatchListItemRequest
import com.bcasekuritas.rabbitmq.proto.bcas.RemoveUserWatchListItemResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleAllUserWatchListRequest
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleAllUserWatchListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleUserWatchListRequest
import com.bcasekuritas.rabbitmq.proto.bcas.SimpleUserWatchListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.UserWatchListGroup
import com.bcasekuritas.rabbitmq.proto.bcas.UserWatchListItem
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageRequest
import com.bcasekuritas.rabbitmq.proto.datafeed.CurrentMessageResponse
import com.bcasekuritas.rabbitmq.proto.datafeed.MIMessage
import com.bcasekuritas.rabbitmq.proto.datafeed.MIType
import com.google.protobuf.GeneratedMessage
import javax.inject.Inject

class WatchlistDataSourceImpl @Inject constructor(
    private val oltService: OLTService
) : WatchlistDataSource {
//    override suspend fun getStockWatchList(watchListRequests: WatchListRequest): GetWatchListResponse? {
//        val watchListReq = GetWatchListRequest
//            .newBuilder()
//            .setUserId(watchListRequests.userId)
//            .setSessionId(watchListRequests.sessionId)
//            .build()
//
//        return oltService.getWatchlist(arrayOf(watchListReq))
//    }

    override suspend fun addStockWatchList(addWatchListRequest: StockWatchListRequest): CurrentMessageResponse? {
        val addWatchListReq = CurrentMessageRequest
            .newBuilder()
            .setUserId(addWatchListRequest.userId)
            .setSessionId(addWatchListRequest.sessionId)
            .setBoardCode(addWatchListRequest.board)
            .setDataType(MIType.TRADE_SUMMARY)
            .addAllItemCode(addWatchListRequest.stockCodeList)
            .build()

        return oltService.getCurrentMessage(addWatchListReq)
    }

    override suspend fun getUserWatchList(userWatchListRequest: UserWatchListRequest): SimpleUserWatchListResponse? {
        val simpleUserWatchListRequest = SimpleUserWatchListRequest
            .newBuilder()
            .setUserId(userWatchListRequest.userId)
            .setWlgCode(userWatchListRequest.wlgCode)
            .setSessionId(userWatchListRequest.sessionId)
            .setIncludeWLItem(true)
            .build()

        return oltService.getUserWatchList(simpleUserWatchListRequest)
    }

    override suspend fun getAllUserWatchlist(userWatchlistRequest: AllUserWatchListRequest): SimpleAllUserWatchListResponse? {
        val simpleAllUserWatchListRequest = SimpleAllUserWatchListRequest.newBuilder()
            .setUserId(userWatchlistRequest.userId)
            .setSessionId(userWatchlistRequest.sessionId)
            .build()

        return oltService.getAllUserWatchlist(simpleAllUserWatchListRequest)
    }

    override suspend fun addUserWatchList(userWatchListRequest: UserWatchListRequest): AddUserWatchListGroupResponse? {
        val userWatchListGroup = UserWatchListGroup
            .newBuilder()
            .setWlCode(userWatchListRequest.wlCode)
            .build()

        val userWatchListItems: List<UserWatchListItem>? =
            userWatchListRequest.userWlListItem?.withIndex()?.map { (index, value) ->
                UserWatchListItem.newBuilder()
                    .setItemSeq(index+1)
                    .setItemCode(value)
                    .build()
            }

        val addUserWatchListGroupRequest = AddUserWatchListGroupRequest
            .newBuilder()
            .setUserId(userWatchListRequest.userId)
            .setSessionId(userWatchListRequest.sessionId)
            .setUserWatchListGroup(userWatchListGroup)
            .addAllUserWatchListItem(userWatchListItems)
            .setWlCodeNew(userWatchListRequest.newWlCode)
            .build()

        return oltService.addWatchListCategory(addUserWatchListGroupRequest)
    }

    override suspend fun addItemCategory(userWatchListRequest: UserWatchListRequest): AddUserWatchListItemResponse? {
        val userWatchListGroup: List<UserWatchListGroup>? =
            userWatchListRequest.userWlListCat?.map {
                UserWatchListGroup
                    .newBuilder()
                    .setWlCode(it)
                    .build()
            }

        val userWatchListItems: List<UserWatchListItem>? =
            userWatchListRequest.userWlListItem?.withIndex()?.map { (index, value) ->
                UserWatchListItem.newBuilder()
                    .setItemSeq(index)
                    .setItemCode(value)
                    .build()
            }

        val addUserWatchListGroupRequest = AddUserWatchListItemRequest
            .newBuilder()
            .setUserId(userWatchListRequest.userId)
            .setSessionId(userWatchListRequest.sessionId)
            .addAllUserWatchListGroup(userWatchListGroup)
            .addAllUserWatchListItem(userWatchListItems)
            .build()

        return oltService.addItemCategory(addUserWatchListGroupRequest)
    }

    override suspend fun removeWatchListCategory(userWatchListRequest: UserWatchListRequest): RemoveUserWatchListGroupResponse? {
        val userWatchListGroup = UserWatchListGroup
            .newBuilder()
            .setWlCode(userWatchListRequest.wlCode)
            .build()

        val addUserWatchListGroupRequest = RemoveUserWatchListGroupRequest
            .newBuilder()
            .setUserId(userWatchListRequest.userId)
            .setSessionId(userWatchListRequest.sessionId)
            .setUserWatchListGroup(userWatchListGroup)
            .build()

        return oltService.removeWatchListCategory(addUserWatchListGroupRequest)
    }

    override suspend fun removeItemCategory(userWatchListRequest: UserWatchListRequest): RemoveUserWatchListItemResponse? {
        val userWatchListGroup = UserWatchListGroup
            .newBuilder()
            .setWlCode(userWatchListRequest.wlCode)
            .build()

        val userWatchListItems = UserWatchListItem.newBuilder()
            .setItemSeq(userWatchListRequest.itemSeq!!)
            .setItemCode(userWatchListRequest.itemCode)
            .build()

        val addUserWatchListGroupRequest = RemoveUserWatchListItemRequest
            .newBuilder()
            .setUserId(userWatchListRequest.userId)
            .setSessionId(userWatchListRequest.sessionId)
            .setUserWatchListGroup(userWatchListGroup)
            .setUserWatchListItem(userWatchListItems)
            .build()

        return oltService.removeItemCategory(addUserWatchListGroupRequest)
    }

    override suspend fun getStockDetail(stockDetail: StockWatchListRequest): CurrentMessageResponse? {
        val stockDetailReq = CurrentMessageRequest
            .newBuilder()
            .setUserId(stockDetail.userId)
            .setSessionId(stockDetail.sessionId)
            .setBoardCode(stockDetail.board)
            .setDataType(MIType.TRADE_SUMMARY)
            .addAllItemCode(stockDetail.stockCodeList)
            .build()

        return oltService.getTradeSummaryCurrentMessage(stockDetailReq)
    }

    override suspend fun startTradeSum() {
        oltService.startTradeSum()
    }

    override suspend fun setListenerTradeSum(miListener: MQMessageListener<MIMessage>?) {
        oltService.setListenerTradeSum(miListener)
    }

    override suspend fun subscribeAllTradeSum(routingKeys: List<String>) {
        oltService.subscribeAllTradeSum(routingKeys)
    }

    override suspend fun subscribeTradeSum(routingKey: String) {
        oltService.subscribeTradeSum(routingKey)
    }

    override suspend fun unSubscribeAllTradeSum(routingKeys: List<String>) {
        oltService.unsubscribeAllTradeSum(routingKeys)
    }

    override suspend fun unSubscribeTradeSum(routingKey: String) {
        oltService.unsubscribeTradeSum(routingKey)
    }

    override suspend fun stopTradeSum() {
        oltService.stopTradeSum()
    }
}