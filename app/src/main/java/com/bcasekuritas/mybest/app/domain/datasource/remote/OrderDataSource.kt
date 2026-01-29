package com.bcasekuritas.mybest.app.domain.datasource.remote

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
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.bcas.AdvancedOrderInfoResponse
import com.bcasekuritas.rabbitmq.proto.bcas.CakraMessage
import com.bcasekuritas.rabbitmq.proto.bcas.MaxOrderByStockResponse
import com.bcasekuritas.rabbitmq.proto.bcas.OrderListHistoryResponse
import com.bcasekuritas.rabbitmq.proto.bcas.OrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryGroupDetailResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryGroupResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListResponse

interface OrderDataSource {

    suspend fun sendOrder(oltOrder: SendOrderReq?)
    suspend fun sendAdvOrder(advOrder: AdvanceOrderRequest?)
    suspend fun amendOrder(amendOltOrder: AmendOrderRequest?)
    suspend fun withdrawOrder(cancelOltOrder: WithdrawOrderRequest?)
    suspend fun getOrderList(orderListRequest: OrderListRequest): OrderListResponse?
    suspend fun sliceOrder(sliceOrderRequest: SliceOrderRequest?)
    suspend fun getMaxOrderByStock(maxOrderByStockReq: MaxOrderByStockReq): MaxOrderByStockResponse?
    suspend fun getOrderHistory(orderHistoryRequest: OrderHistoryRequest): OrderListHistoryResponse?
    suspend fun startOrderReply(cakraListener: MQMessageListener<CakraMessage>?, accNo: String?)
    suspend fun subscribeOrderReply(accNo: String?)
    suspend fun unsubscribeOrderReply(accNo: String?)
    suspend fun stopOrderReply()
    suspend fun startNewOrderReply(orderReplyFlow: (CakraMessage) -> Unit)
    suspend fun insertOrderReply(orderReplyObject: OrderReplyObject)
    suspend fun getOrderReplyDao(clOrderRef: String): OrderReplyObject
    suspend fun getAdvanceOrderInfo(advanceOrderListRequest: AdvanceOrderListRequest): AdvancedOrderInfoResponse?
    suspend fun sendAutoOrder(autoOrderRequest: AutoOrderRequest)

    suspend fun withdrawAdvancedOrder(cancelOltOrder: WithdrawOrderRequest?)

    suspend fun getTradeList(tradeListReq: TradeListReq): TradeListResponse?
    suspend fun getTradeListHistory(tradeListHistoryReq: TradeListHistoryReq): TradeListHistoryResponse?

    /** Fast Order*/
    suspend fun sendWithdrawFastOrder(cancelFastOrderReq: CancelFastOrderReq)
    suspend fun sendAllWithdrawFastOrder(cancelFastOrderReq: CancelFastOrderReq)
    suspend fun sendAmendFastOrder(amendFastOrderReq: AmendFastOrderReq)

    /** E-IPO Order*/
    suspend fun sendEipoOrder(eipoOrderRequest: EipoOrderRequest)

    /** Trade List History Group*/
    suspend fun getTradeListHistoryGroup(tradeListHistoryReq: TradeListHistoryReq): TradeListHistoryGroupResponse?
    suspend fun getTradeListHistoryGroupDetail(tradeListHistoryDetailReq: TradeListHistoryDetailReq): TradeListHistoryGroupDetailResponse?

  
}