package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.data.dao.OrderReplyDao
import com.bcasekuritas.mybest.app.data.entity.OrderReplyObject
import com.bcasekuritas.mybest.app.domain.datasource.remote.OrderDataSource
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
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.bcas.AdvancedCriteria
import com.bcasekuritas.rabbitmq.proto.bcas.AdvancedOrderInfoRequest
import com.bcasekuritas.rabbitmq.proto.bcas.AdvancedOrderInfoResponse
import com.bcasekuritas.rabbitmq.proto.bcas.AmendFastOrderRequest
import com.bcasekuritas.rabbitmq.proto.bcas.AmendOLTOrderRequest
import com.bcasekuritas.rabbitmq.proto.bcas.CakraMessage
import com.bcasekuritas.rabbitmq.proto.bcas.CancelFastOrderRequest
import com.bcasekuritas.rabbitmq.proto.bcas.CancelOLTOrderRequest
import com.bcasekuritas.rabbitmq.proto.bcas.IpoEquityOrder
import com.bcasekuritas.rabbitmq.proto.bcas.MaxOrderByStockRequest
import com.bcasekuritas.rabbitmq.proto.bcas.MaxOrderByStockResponse
import com.bcasekuritas.rabbitmq.proto.bcas.NewAdvancedBracketOrder
import com.bcasekuritas.rabbitmq.proto.bcas.NewAdvancedOrder
import com.bcasekuritas.rabbitmq.proto.bcas.NewOLTOrder
import com.bcasekuritas.rabbitmq.proto.bcas.OrderListHistoryRequest
import com.bcasekuritas.rabbitmq.proto.bcas.OrderListHistoryResponse
import com.bcasekuritas.rabbitmq.proto.bcas.OrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.PageRequest
import com.bcasekuritas.rabbitmq.proto.bcas.StopAdvancedOrder
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryGroupDetailRequest
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryGroupDetailResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryGroupRequest
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryGroupResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryRequest
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListHistoryResponse
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListRequest
import com.bcasekuritas.rabbitmq.proto.bcas.TradeListResponse
import java.util.Date
import javax.inject.Inject

class OrderDataSourceImpl @Inject constructor(
    private val oltService: OLTService,
    private val localSourceData: OrderReplyDao
) : OrderDataSource {
    override suspend fun sendOrder(oltOrder: SendOrderReq?) {
        val bNewOltOrder = NewOLTOrder.newBuilder()
            .setClOrderRef(oltOrder!!.clOrderRef)
            .setBoard(oltOrder.board)
            .setOrderTime(oltOrder.orderTime!!)
            .setBuySell(oltOrder.buySell)
            .setStockCode(oltOrder.stockCode)
            .setOrderType(oltOrder.orderType)
            .setTimeInForce(oltOrder.timeInForce)
            .setAccType(oltOrder.accType)
            .setOrdQty(oltOrder.ordQty!!)
            .setOrdPrice(oltOrder.ordPrice!!)
            .setAccNo(oltOrder.accNo)
            .setInputBy(oltOrder.inputBy)
            .setCheckSameOrder(false)
            .setOrderPeriod(oltOrder.orderPeriod!!)
            .setMediaSource(0)
            .setIpAddress(oltOrder.ip)

        val cakraMessage = CakraMessage
            .newBuilder()
            .setType(CakraMessage.Type.NEW_OLT_ORDER)
            .setNewOLTOrder(bNewOltOrder)
            .setUserId(oltOrder.inputBy)
            .setSendingTime(Date().time)
            .setSessionId(oltOrder.sessionId)
            .build()

        oltService.sendOrder(cakraMessage)
    }



    override suspend fun sendAdvOrder(advOrder: AdvanceOrderRequest?) {
        val bracketCriteriaVal = advOrder?.bracketCriteria
        val takeProfitVal = advOrder?.takeProfitCriteria
        val stopLossVal = advOrder?.stopLossCriteria

        val bracketCriteria = AdvancedCriteria.newBuilder()
            .setAdvType(bracketCriteriaVal!!.advType)
            .setTriggerOrder(bracketCriteriaVal.triggerOrder)
            .setTriggerVal(bracketCriteriaVal.triggerVal)
            .setTriggerCategory(bracketCriteriaVal.opr)
            .setTriggerCategory(bracketCriteriaVal.triggerCategory)

        val takeProfitCriteria = AdvancedCriteria.newBuilder()
            .setAdvType(takeProfitVal!!.advType)
            .setOpr(takeProfitVal.opr)
            .setTriggerVal(takeProfitVal.triggerVal)
            .setTriggerCategory(takeProfitVal.triggerCategory)
            .setTriggerOrder(takeProfitVal.triggerOrder)

        val stopLossCriteria = AdvancedCriteria.newBuilder()
            .setAdvType(stopLossVal!!.advType)
            .setOpr(stopLossVal.opr)
            .setTriggerVal(stopLossVal.triggerVal)
            .setTriggerCategory(stopLossVal.triggerCategory)
            .setTriggerOrder(stopLossVal.triggerOrder)

        val advanceOrder = NewAdvancedBracketOrder.newBuilder().apply {
            setClOrderRef(advOrder.clOrderRef)
            setAccNo(advOrder.accNo)
            setAccType(advOrder.accType)
            setAdvType(advOrder.advType)
            setStockCode(advOrder.stockCode)
            setOrdQty(advOrder.ordQty)
            if (bracketCriteriaVal.advType != 0) {
                setBracketCriteria(bracketCriteria)
            }
            if (takeProfitVal.triggerVal != 0L) {
                setTakeProfitCriteria(takeProfitCriteria)
            }
            if (stopLossVal.triggerVal != 0L) {
                setStopLossCriteria(stopLossCriteria)
            }
            setInputBy(advOrder.inputBy)
            setMediaSource(0)
            setIpAddress(advOrder.ip)
        }

        val cakraMessage = CakraMessage
            .newBuilder()
            .setType(CakraMessage.Type.NEW_ADVANCED_BRACKET_ORDER)
            .setNewAdvancedBracketOrder(advanceOrder)
            .setUserId(advOrder.inputBy)
            .setSendingTime(Date().time)
            .setSessionId(advOrder.sessionId)
            .build()

        oltService.sendOrder(cakraMessage)
    }

    override suspend fun amendOrder(amendOltOrder: AmendOrderRequest?) {
        val amendOLTOrderRequest = AmendOLTOrderRequest.newBuilder()
            .setNewCliOrderRef(amendOltOrder!!.newCliOrderRef)
            .setOldCliOrderRef(amendOltOrder.oldCliOrderRef)
            .setOrderID(amendOltOrder.orderID)
            .setNewQty(amendOltOrder.newQty ?: 0.0)
            .setNewPrice(amendOltOrder.newPrice ?: 0.0)
            .setNewOrdPeriod(amendOltOrder.newOrdPeriod ?: 0)
            .setInputBy(amendOltOrder.inputBy)
            .setIpAddress(amendOltOrder.ip)
            .setAccNo(amendOltOrder.accNo)
            .setNewTimeInForce(amendOltOrder.newTimeInForce)
            .build()

        val cakraMessage = CakraMessage
            .newBuilder()
            .setType(CakraMessage.Type.AMEND_OLT_ORDER_REQUEST)
            .setAmendOLTOrderRequest(amendOLTOrderRequest)
            .setUserId(amendOltOrder.inputBy)
            .setSendingTime(Date().time)
            .setSessionId(amendOltOrder.sessionId)
            .build()

        oltService.sendOrder(cakraMessage)
    }

    override suspend fun withdrawOrder(cancelOltOrder: WithdrawOrderRequest?) {
        val cancelOLTOrderRequest = CancelOLTOrderRequest
            .newBuilder()
            .setNewCliOrderRef(cancelOltOrder!!.newCliOrderRef)
            .setOldCliOrderRef(cancelOltOrder.oldCliOrderRef)
            .setOrderID(cancelOltOrder.orderID)
            .setInputBy(cancelOltOrder.inputBy)
            .setIpAddress(cancelOltOrder.ip)
            .setAccNo(cancelOltOrder.accNo)
            .build()

        val cakraMessage = CakraMessage
            .newBuilder()
            .setType(CakraMessage.Type.CANCEL_OLT_ORDER_REQUEST)
            .setCancelOLTOrderRequest(cancelOLTOrderRequest)
            .setUserId(cancelOltOrder.inputBy)
            .setSessionId(cancelOltOrder.sessionId)
            .setSendingTime(Date().time)
            .build()

        oltService.sendOrder(cakraMessage)
    }

    override suspend fun withdrawAdvancedOrder(cancelOltOrder: WithdrawOrderRequest?) {
        val cancelAdvancedOrderRequest = StopAdvancedOrder.newBuilder()
            .setClOrderRef(cancelOltOrder?.orderID)
            .setOrderId(cancelOltOrder?.orderID)
            .setInputBy(cancelOltOrder?.inputBy)
            .build()

        val cakraMessage = CakraMessage
            .newBuilder()
            .setType(CakraMessage.Type.STOP_ADVANCED_ORDER)
            .setStopAdvancedOrder(cancelAdvancedOrderRequest)
            .setUserId(cancelOltOrder?.inputBy)
            .setSessionId(cancelOltOrder?.sessionId)
            .setSendingTime(Date().time)
            .build()

        oltService.sendOrder(cakraMessage)
    }

    override suspend fun getOrderList(orderListRequest: OrderListRequest): OrderListResponse? {
        val orderListReq = com.bcasekuritas.rabbitmq.proto.bcas.OrderListRequest
            .newBuilder()
            .setUserId(orderListRequest.userId)
            .setAccNo(orderListRequest.accNo)
            .setSessionId(orderListRequest.sessionId)
            .setIncludeTrade(orderListRequest.includeTrade)
            .build()

        return oltService.getOrderListInfo(orderListReq)
    }

    override suspend fun getTradeList(tradeListReq: TradeListReq): TradeListResponse? {
        val tradeListRequest = TradeListRequest.newBuilder()
            .setUserId(tradeListReq.userId)
            .setAccNo(tradeListReq.accNo)
            .setSessionId(tradeListReq.sessionId)
            .build()

        return oltService.getTradeList(tradeListRequest)
    }

    override suspend fun sliceOrder(sliceOrderRequest: SliceOrderRequest?) {

        if (sliceOrderRequest?.splitBlockSize != 0) {
            val newAdvanceOrder = NewAdvancedOrder.newBuilder()
                .setClOrderRef(sliceOrderRequest?.clOrderRef)
                .setAccNo(sliceOrderRequest?.accNo)
                .setAdvType(sliceOrderRequest?.advType!!)
                .setStockCode(sliceOrderRequest.stockCode)
                .setBuySell(sliceOrderRequest.buySell)
                .setOrdType(sliceOrderRequest.ordType!!)
                .setOrdPrice(sliceOrderRequest.ordPrice!!)
                .setSplitNumber(sliceOrderRequest.splitNumber!!)
                .setInputBy(sliceOrderRequest.inputBy)
                .setIpAddress(sliceOrderRequest.ipAddress)
                .setAccType(sliceOrderRequest.accType)
                .setChannel(sliceOrderRequest.channel!!)
                .setSplitBlockSize(sliceOrderRequest.splitBlockSize!!)
                .setMediaSource(sliceOrderRequest.mediaSource)
                .setEndTriggerTime(sliceOrderRequest.endTriggerTime?: 0L)
                .build()

            val cakraMessage = CakraMessage
                .newBuilder()
                .setType(CakraMessage.Type.NEW_ADVANCED_ORDER)
                .setNewAdvancedOrder(newAdvanceOrder)
                .setUserId(newAdvanceOrder.inputBy) //.setSessionId(cancelOltOrder.getSessionId())
                .setSendingTime(Date().time)
                .setSessionId(sliceOrderRequest.sessionId)
                .build()

            oltService.sendOrder(cakraMessage)
        } else {
            val newAdvanceOrder = NewAdvancedOrder.newBuilder()
                .setClOrderRef(sliceOrderRequest?.clOrderRef)
                .setAccNo(sliceOrderRequest?.accNo)
                .setAdvType(sliceOrderRequest?.advType!!)
                .setStockCode(sliceOrderRequest.stockCode)
                .setBuySell(sliceOrderRequest.buySell)
                .setOrdType(sliceOrderRequest.ordType!!)
                .setOrdQty(sliceOrderRequest.ordQty!!)
                .setOrdPrice(sliceOrderRequest.ordPrice!!)
                .setSplitNumber(sliceOrderRequest.splitNumber!!)
                .setInputBy(sliceOrderRequest.inputBy)
                .setIpAddress(sliceOrderRequest.ipAddress)
                .setAccType(sliceOrderRequest.accType)
                .setChannel(sliceOrderRequest.channel!!)
                .setSplitBlockSize(sliceOrderRequest.splitBlockSize!!)
                .setMediaSource(sliceOrderRequest.mediaSource)
                .setEndTriggerTime(sliceOrderRequest.endTriggerTime?: 0L)
                .build()

            val cakraMessage = CakraMessage
                .newBuilder()
                .setType(CakraMessage.Type.NEW_ADVANCED_ORDER)
                .setNewAdvancedOrder(newAdvanceOrder)
                .setUserId(newAdvanceOrder.inputBy) //.setSessionId(cancelOltOrder.getSessionId())
                .setSendingTime(Date().time)
                .setSessionId(sliceOrderRequest.sessionId)
                .build()

            oltService.sendOrder(cakraMessage)
        }

    }

    override suspend fun sendAutoOrder(autoOrderRequest: AutoOrderRequest) {
        val newAdvanceOrder = NewAdvancedOrder.newBuilder()
            .setClOrderRef(autoOrderRequest?.clOrderRef)
            .setAccNo(autoOrderRequest?.accNo)
            .setAdvType(2)
            .setStockCode(autoOrderRequest.stockCode)
            .setBuySell("B")
            .setTriggerCategory(0)
            .setTimeInForce(autoOrderRequest.timeInForce)
            .setValidUntil(autoOrderRequest.validUntil)
            .setOrdQty(autoOrderRequest.ordQty)
            .setOrdPrice(autoOrderRequest.ordPrice)
            .setOpr(autoOrderRequest.opr)
            .setTriggerVal(autoOrderRequest.triggerval)
            .setInputBy(autoOrderRequest.inputBy)
            .setIpAddress(autoOrderRequest.ipAddress)
            .setAccType(autoOrderRequest.accType)
            .setChannel(autoOrderRequest.channel!!)
            .setMediaSource(0)
            .build()

        val cakraMessage = CakraMessage
            .newBuilder()
            .setType(CakraMessage.Type.NEW_ADVANCED_ORDER)
            .setNewAdvancedOrder(newAdvanceOrder)
            .setUserId(autoOrderRequest.inputBy) //.setSessionId(cancelOltOrder.getSessionId())
            .setSendingTime(Date().time)
            .setSessionId(autoOrderRequest.sessionId)
            .build()

        oltService.sendOrder(cakraMessage)

    }

    override suspend fun getOrderHistory(orderHistoryRequest: OrderHistoryRequest): OrderListHistoryResponse? {
        val orderHistoryReq = OrderListHistoryRequest
            .newBuilder().apply {
                setUserId(orderHistoryRequest.userId)
                setAccNo(orderHistoryRequest.accNo)
                setSessionId(orderHistoryRequest.sessionId)
                setIncludeTrade(orderHistoryRequest.includeTrade)
                setStartDate(orderHistoryRequest.startDate)
                setEndDate(orderHistoryRequest.endDate)
                if (orderHistoryRequest.stockCode.isNotEmpty()) {
                    setStockCode(orderHistoryRequest.stockCode)
                } else {
                    setStockCode("*")
                }
            }.build()

        return oltService.getHistoryOrder(orderHistoryReq)
    }

    override suspend fun getMaxOrderByStock(getMaxOrderByStockRequest: MaxOrderByStockReq): MaxOrderByStockResponse? {
            val maxOrderReq = MaxOrderByStockRequest
                .newBuilder().apply {
                    setUserId(getMaxOrderByStockRequest.userId)
                    setAccNo(getMaxOrderByStockRequest.accNo)
                    setSessionId(getMaxOrderByStockRequest.sessionId)
                    setBuySell(getMaxOrderByStockRequest.buySell)
                    setStockCode(getMaxOrderByStockRequest.stockCode)
                    setPrice(getMaxOrderByStockRequest.price ?: 0.0)
                    setBuyType(getMaxOrderByStockRequest.buyType)
                    setBoardCode(getMaxOrderByStockRequest.boardCode)
                    if (getMaxOrderByStockRequest.relId != "") {
                        setRelId(getMaxOrderByStockRequest.relId)
                    }
                }.build()


            return oltService.getMaxOrderByStock(maxOrderReq)
    }

    override suspend fun getAdvanceOrderInfo(advanceOrderListRequest: AdvanceOrderListRequest): AdvancedOrderInfoResponse? {
        val advancedOrderInfoRequest = AdvancedOrderInfoRequest.newBuilder()
            .setUserId(advanceOrderListRequest.userId)
            .setAccNo(advanceOrderListRequest.accNo)
            .setIncludeOrderInfo(advanceOrderListRequest.includeOrderInfo)
            .build()

        return oltService.getAdvanceOrderInfo(advancedOrderInfoRequest)
    }

    override suspend fun startOrderReply(
        cakraListener: MQMessageListener<CakraMessage>?,
        accNo: String?
    ) {
        oltService.startConsumeOrderReply(accNo, cakraListener)
    }

    override suspend fun subscribeOrderReply(accNo: String?) {
        oltService.subscribeOrderReply(accNo)
    }

    override suspend fun unsubscribeOrderReply(accNo: String?) {
        oltService.unsubscribeOrderReply(accNo)
    }

    override suspend fun stopOrderReply() {
        oltService.stopConsumeOrderReply()
    }

    override suspend fun startNewOrderReply(orderReplyFlow: (CakraMessage) -> Unit) {
        oltService.startNewOrderReply(orderReplyFlow)
    }

    override suspend fun insertOrderReply(orderReplyObject: OrderReplyObject) {
        orderReplyObject.let {
            localSourceData.insertOrderReply(
                OrderReplyObject(
                    clOrderRef = it.clOrderRef,
                    accNo = it.accNo,
                    status = it.status,
                    lotSize = it.lotSize,
                    orderID = it.orderID,
                    orderTime = it.orderTime,
                    buySell = it.buySell,
                    stockCode = it.stockCode,
                    insvtType = it.insvtType,
                    ordQty = it.ordQty,
                    matchQty = it.matchQty,
                    ordPrice = it.ordPrice,
                    timeInForce = it.timeInForce,
                    clientCode = it.clientCode,
                    clientSID = it.clientSID,
                    orderPeriod = it.orderPeriod,
                    inputBy = it.inputBy,
                    remarks = it.remarks,
                    rejectReason = it.rejectReason,
                    exOrderId = it.exOrderId,
                    accType = it.accType,
                    orderType = it.orderType,
                    oldClOrderRef = it.oldClOrderRef,
                    oldOrderId = it.oldOrderId,
                    checkSameOrder = it.checkSameOrder
                )
            )
        }
    }

    override suspend fun getOrderReplyDao(clOrderRef: String): OrderReplyObject {
        return localSourceData.getOrderReply(clOrderRef)
    }

    override suspend fun sendWithdrawFastOrder(cancelFastOrderReq: CancelFastOrderReq) {
        val cancelFastOrder = CancelFastOrderRequest.newBuilder()
            .setStock(cancelFastOrderReq.stock)
            .setBoard(cancelFastOrderReq.board)
            .setBuySell(cancelFastOrderReq.buySell)
            .setPrice(cancelFastOrderReq.price ?: 0.0)
            .setInputBy(cancelFastOrderReq.inputBy)
            .setIpAddress(cancelFastOrderReq.ipAddress)
            .setChannel(0)//0=OLT, 1=OMS, 7=CNA
            .setAccNo(cancelFastOrderReq.accNo)

        val cakraMessage = CakraMessage
            .newBuilder()
            .setType(CakraMessage.Type.CANCEL_FAST_ORDER_REQUEST)
            .setCancelFastOrderRequest(cancelFastOrder)
            .setUserId(cancelFastOrderReq.inputBy)
            .setSendingTime(Date().time)
            .setSessionId(cancelFastOrderReq.sessionId)
            .build()

        oltService.sendOrder(cakraMessage)
    }

    override suspend fun sendAllWithdrawFastOrder(cancelFastOrderReq: CancelFastOrderReq) {
        val cancelFastOrder = CancelFastOrderRequest.newBuilder()
            .setStock(cancelFastOrderReq.stock)
            .setBoard(cancelFastOrderReq.board)
            .setBuySell(cancelFastOrderReq.buySell)
            .setInputBy(cancelFastOrderReq.inputBy)
            .setIpAddress(cancelFastOrderReq.ipAddress)
            .setChannel(0)//0=OLT, 1=OMS, 7=CNA
            .setAccNo(cancelFastOrderReq.accNo)

        val cakraMessage = CakraMessage
            .newBuilder()
            .setType(CakraMessage.Type.CANCEL_FAST_ORDER_REQUEST)
            .setCancelFastOrderRequest(cancelFastOrder)
            .setUserId(cancelFastOrderReq.inputBy)
            .setSendingTime(Date().time)
            .setSessionId(cancelFastOrderReq.sessionId)
            .build()

        oltService.sendOrder(cakraMessage)
    }

    override suspend fun sendAmendFastOrder(amendFastOrderReq: AmendFastOrderReq) {
        val amendFastOrder = AmendFastOrderRequest.newBuilder()
            .setStock(amendFastOrderReq.stock)
            .setBoard(amendFastOrderReq.board)
            .setBuySell(amendFastOrderReq.buySell)
            .setOldPrice(amendFastOrderReq.oldPrice ?: 0.0)
            .setNewPrice(amendFastOrderReq.newPrice ?: 0.0)
            .setInputBy(amendFastOrderReq.inputBy)
            .setIpAddress(amendFastOrderReq.ipAddress)
            .setChannel(0)//0=OLT, 1=OMS, 7=CNA
            .setAccNo(amendFastOrderReq.accNo)

        val cakraMessage = CakraMessage
            .newBuilder()
            .setType(CakraMessage.Type.AMEND_FAST_ORDER_REQUEST)
            .setAmendFastOrderRequest(amendFastOrder)
            .setUserId(amendFastOrderReq.inputBy)
            .setSendingTime(Date().time)
            .setSessionId(amendFastOrderReq.sessionId)
            .build()

        oltService.sendOrder(cakraMessage)
    }

    override suspend fun getTradeListHistory(tradeListHistoryReq: TradeListHistoryReq): TradeListHistoryResponse? {
        val pageRequest = PageRequest.newBuilder()
            .setPage(tradeListHistoryReq.pageRequest.page)
            .setSize(tradeListHistoryReq.pageRequest.size)
            .build()

        val tradeListHistory = TradeListHistoryRequest.newBuilder()
            .setUserId(tradeListHistoryReq.userId)
            .setAccNo(tradeListHistoryReq.accNo)
            .setSessionId(tradeListHistoryReq.sessionId)
            .setStartDate(tradeListHistoryReq.startDate)
            .setEndDate(tradeListHistoryReq.endDate)
            .setStockCode(tradeListHistoryReq.stockCode)
            .setPageRequest(pageRequest)
            .build()

        return oltService.getTradeListHistory(tradeListHistory)
    }

    override suspend fun sendEipoOrder(eipoOrderRequest: EipoOrderRequest) {
        val request = IpoEquityOrder.newBuilder()
            .setClOrderRef(eipoOrderRequest.clOrderRef)
            .setAccNo(eipoOrderRequest.accNo)
            .setIpoCode(eipoOrderRequest.eipoCode)
            .setPrice(eipoOrderRequest.price?: 0.0)
            .setQty(eipoOrderRequest.qty?: 0.0)
            .setDesc("")
            .setInputBy(eipoOrderRequest.userId)
            .setIpAddress(eipoOrderRequest.ipAddress)
            .setChannel(0)
            .setMediaSource(0)
            .setTypeId(0)
            .setIsAffiliatedParty(eipoOrderRequest.isAffiliatedParty)
            .setIsEmployee(eipoOrderRequest.isEmployee)
            .setIsBenefaciaries(eipoOrderRequest.isBenefaciaries)
            .build()

        val cakraMessage = CakraMessage.newBuilder()
            .setType(CakraMessage.Type.EIPO_EQUITY_ORDER)
            .setSendingTime(eipoOrderRequest.orderTime?: 0L)
            .setUserId(eipoOrderRequest.userId)
            .setSessionId(eipoOrderRequest.sessionId)
            .setIpoEquityOrder(request)
            .build()


        return oltService.sendOrder(cakraMessage)
    }

    override suspend fun getTradeListHistoryGroup(tradeListHistoryReq: TradeListHistoryReq): TradeListHistoryGroupResponse? {
        val pageRequest = PageRequest.newBuilder()
            .setPage(tradeListHistoryReq.pageRequest.page)
            .setSize(tradeListHistoryReq.pageRequest.size)
            .build()

        val request = TradeListHistoryGroupRequest.newBuilder()
            .setUserId(tradeListHistoryReq.userId)
            .setSessionId(tradeListHistoryReq.sessionId)
            .setAccNo(tradeListHistoryReq.accNo)
            .setStartDate(tradeListHistoryReq.startDate)
            .setEndDate(tradeListHistoryReq.endDate)
            .setStockCode(tradeListHistoryReq.stockCode)
            .setPageRequest(pageRequest)
            .build()

        return oltService.getTradeHistoryGroup(request)
    }

    override suspend fun getTradeListHistoryGroupDetail(tradeListHistoryDetailReq: TradeListHistoryDetailReq): TradeListHistoryGroupDetailResponse? {
        val request = TradeListHistoryGroupDetailRequest.newBuilder()
            .setUserId(tradeListHistoryDetailReq.userId)
            .setSessionId(tradeListHistoryDetailReq.sessionId)
            .setAccno(tradeListHistoryDetailReq.accNo)
            .setExchordid(tradeListHistoryDetailReq.exchordid)
            .setPrice(tradeListHistoryDetailReq.price)
            .build()

        return oltService.getTradeHistoryGroupDetail(request)
    }
}