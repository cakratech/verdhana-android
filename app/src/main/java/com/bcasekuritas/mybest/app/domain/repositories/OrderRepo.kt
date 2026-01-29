package com.bcasekuritas.mybest.app.domain.repositories

import androidx.lifecycle.LiveData
import com.bcasekuritas.mybest.app.data.entity.OrderReplyObject
import com.bcasekuritas.mybest.app.domain.dto.request.AdvanceOrderListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.AdvanceOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.AmendFastOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.AmendOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.AutoOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.CancelFastOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.EipoOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.MaxOrderByStockReq
import com.bcasekuritas.mybest.app.domain.dto.request.OrderHistoryRequest
import com.bcasekuritas.mybest.app.domain.dto.request.OrderListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.SendOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.SliceOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.request.TradeListHistoryDetailReq
import com.bcasekuritas.mybest.app.domain.dto.request.TradeListHistoryReq
import com.bcasekuritas.mybest.app.domain.dto.request.TradeListReq
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.response.FastOrderListInfo
import com.bcasekuritas.mybest.app.domain.dto.response.OltOrder
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.event.SingleLiveEvent
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.bcas.AdvancedOrderInfoResponse
import com.bcasekuritas.rabbitmq.proto.bcas.CakraMessage
import com.bcasekuritas.rabbitmq.proto.bcas.MaxOrderByStockResponse
import com.bcasekuritas.rabbitmq.proto.bcas.OrderInfo
import com.bcasekuritas.rabbitmq.proto.bcas.OrderListHistoryResponse
import com.bcasekuritas.rabbitmq.proto.bcas.OrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryGroupDetailResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryGroupResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListResponse
import kotlinx.coroutines.flow.Flow

interface OrderRepo {

    suspend fun sendOrder(newOLTOrder: SendOrderReq?)
    suspend fun sendAdvOrder(advOrder: AdvanceOrderRequest?)
    suspend fun sendAutoOrder(autoOrderRequest: AutoOrderRequest?)
    suspend fun amendOrder(amendOrder: AmendOrderRequest?)
    suspend fun withdrawOrder(withdrawOrder: WithdrawOrderRequest?)

    suspend fun withdrawAdvancedOrder(withdrawOrder: WithdrawOrderRequest?)
    suspend fun getOrderList(orderListRequest: OrderListRequest): Flow<Resource<OrderListResponse?>>
    suspend fun getTradeList(tradeListReq: TradeListReq): Flow<Resource<TradeListResponse?>>
    suspend fun sliceOrder(sliceOrderRequest: SliceOrderRequest?)
    suspend fun getOrderHistory(orderHistoryRequest: OrderHistoryRequest): Flow<Resource<OrderListHistoryResponse?>>
    suspend fun getMaxOrderByStock(maxOrderByStockReq: MaxOrderByStockReq): Flow<Resource<MaxOrderByStockResponse?>>
    suspend fun startOrderReply(cakraListener: MQMessageListener<CakraMessage>?, accNo: String?)

    suspend fun subscribeOrderReply(accNo: String?)
    suspend fun unsubscribeOrderReply(accNo: String?)
    suspend fun stopOrderReply()

    suspend fun startNewOrderReply()
    suspend fun clearOrderReplyLiveData()

    val getOrderMatch: LiveData<String>

    val orderReplyLiveData: LiveData<OltOrder>

    val newOrderNotifLiveData: SingleLiveEvent<Boolean>

    val fastOrderLiveData: LiveData<FastOrderListInfo>

    val getOrderEipo: LiveData<String>

    val orderReplyList: MutableMap<String, OltOrder>

    suspend fun insertOrderReply(orderReplyObject: OrderReplyObject)
    suspend fun getOrderReplyDao(clOrderRef: String): Flow<Resource<OrderReplyObject>>
    suspend fun putOrderReply(orderList: List<OrderInfo>)

    suspend fun getAdvanceOrderInfo(advanceOrderListRequest: AdvanceOrderListRequest): Flow<Resource<AdvancedOrderInfoResponse>>
    suspend fun getTradeListHistory(tradeListHistoryReq: TradeListHistoryReq): Flow<Resource<TradeListHistoryResponse?>>


    /** Fast Order*/
    suspend fun sendWithdrawFastOrder(cancelFastOrderReq: CancelFastOrderReq)
    suspend fun sendAllWithdrawFastOrder(cancelFastOrderReq: CancelFastOrderReq)
    suspend fun sendAmendFastOrder(amendFastOrderReq: AmendFastOrderReq)

    /** E-IPO Order*/
    suspend fun sendEipoOrder(eipoOrderRequest: EipoOrderRequest)

    /** Trade List History Group*/
    suspend fun getTradeListHistoryGroup(tradeListHistoryReq: TradeListHistoryReq): Flow<Resource<TradeListHistoryGroupResponse?>>
    suspend fun getTradeListHistoryGroupDetail(tradeListHistoryDetailReq: TradeListHistoryDetailReq): Flow<Resource<TradeListHistoryGroupDetailResponse?>>

}