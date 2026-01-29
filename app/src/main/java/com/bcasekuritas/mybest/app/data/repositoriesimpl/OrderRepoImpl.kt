package com.bcasekuritas.mybest.app.data.repositoriesimpl

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.bcasekuritas.mybest.app.domain.dto.response.FastOrderByStockInfo
import com.bcasekuritas.mybest.app.domain.dto.response.FastOrderInfo
import com.bcasekuritas.mybest.app.domain.dto.response.FastOrderListInfo
import com.bcasekuritas.mybest.app.domain.dto.response.OltOrder
import com.bcasekuritas.mybest.app.domain.repositories.OrderRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.ext.event.SingleLiveEvent
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OrderRepoImpl @Inject constructor(
    private val remoteSource: OrderDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper,
) : OrderRepo {
    override suspend fun sendOrder(newOLTOrder: SendOrderReq?) {
        remoteSource.sendOrder(newOLTOrder)

        newOLTOrder?.let {
            _orderReplyList[newOLTOrder.clOrderRef ?: ""] = OltOrder(
                clOrderRef = newOLTOrder.clOrderRef ?: "",
                status = newOLTOrder.status ?: "",
                stockCode = newOLTOrder.stockCode ?: "",
                orderType = newOLTOrder.orderType ?: "",
                lotSize = newOLTOrder.ordQty?.div(100)?.toInt() ?: 0,
                ordPrice = newOLTOrder.ordPrice ?: 0.0,
                buySell = newOLTOrder.buySell ?: "",
                timeInForce = newOLTOrder.timeInForce.toString(),
                orderPeriod = newOLTOrder.orderPeriod
            )
        }
    }

    override suspend fun sendAdvOrder(advOrder: AdvanceOrderRequest?) {
        remoteSource.sendAdvOrder(advOrder)
    }

    override suspend fun sendAutoOrder(autoOrderRequest: AutoOrderRequest?) {
        if (autoOrderRequest != null) {
            remoteSource.sendAutoOrder(autoOrderRequest)
        }
    }

    override suspend fun amendOrder(amenOLTOrder: AmendOrderRequest?) {
        remoteSource.amendOrder(amenOLTOrder)

        amenOLTOrder?.let {
            _orderReplyList[amenOLTOrder.newCliOrderRef ?: ""] = OltOrder(
                clOrderRef = amenOLTOrder.newCliOrderRef ?: "",
                oldClOrderRef = amenOLTOrder.orderID ?: "",
                stockCode = amenOLTOrder.stockCode ?: "",
                lotSize = amenOLTOrder.newQty?.toInt(),
                ordPrice = amenOLTOrder.newPrice ?: 0.0,
                timeInForce = amenOLTOrder.newTimeInForce.toString()
            )
        }
    }

    override suspend fun withdrawOrder(withdrawOltOrder: WithdrawOrderRequest?) {
        remoteSource.withdrawOrder(withdrawOltOrder)

        withdrawOltOrder?.let {
        }
    }

    override suspend fun withdrawAdvancedOrder(withdrawOrder: WithdrawOrderRequest?) {
        remoteSource.withdrawAdvancedOrder(withdrawOrder)
    }

    override suspend fun getOrderList(orderListRequest: OrderListRequest): Flow<Resource<OrderListResponse?>> =
        flow {
            emit(
                Resource.Success(
                    data = remoteSource.getOrderList(orderListRequest),
                    DataSource.REMOTE
                )
            )
        }

    override suspend fun getTradeList(tradeListReq: TradeListReq): Flow<Resource<TradeListResponse?>> =
        flow {
            emit(
                Resource.Success(
                    data = remoteSource.getTradeList(tradeListReq),
                    DataSource.REMOTE
                )
            )
        }


    override suspend fun sliceOrder(sliceOrderRequest: SliceOrderRequest?) {
        remoteSource.sliceOrder(sliceOrderRequest)
    }

    override suspend fun getOrderHistory(orderHistoryRequest: OrderHistoryRequest): Flow<Resource<OrderListHistoryResponse?>> =
        flow {
            emit(
                Resource.Success(
                    data = remoteSource.getOrderHistory(orderHistoryRequest),
                    DataSource.REMOTE
                )
            )
        }

    override suspend fun getMaxOrderByStock(maxOrderByStockReq: MaxOrderByStockReq): Flow<Resource<MaxOrderByStockResponse?>> =
        flow {
            emit(
                Resource.Success(
                    data = remoteSource.getMaxOrderByStock(maxOrderByStockReq),
                    DataSource.REMOTE
                )
            )
        }

    override suspend fun getAdvanceOrderInfo(advanceOrderListRequest: AdvanceOrderListRequest): Flow<Resource<AdvancedOrderInfoResponse>> =
        flow {
            emit(
                Resource.Success(
                    data = remoteSource.getAdvanceOrderInfo(advanceOrderListRequest),
                    DataSource.REMOTE
                )
            )
        }

    override suspend fun startOrderReply(
        cakraListener: MQMessageListener<CakraMessage>?,
        accNo: String?,
    ) {
        remoteSource.startOrderReply(cakraListener, accNo)
    }

    override suspend fun subscribeOrderReply(accNo: String?) {
        remoteSource.subscribeOrderReply(accNo)
    }

    override suspend fun unsubscribeOrderReply(accNo: String?) {
        remoteSource.unsubscribeOrderReply(accNo)
    }

    override suspend fun stopOrderReply() {
        remoteSource.stopOrderReply()
    }

    /** ORDERLIST */

    private val _getOrderMatch = MutableLiveData<String>()
    override val getOrderMatch: LiveData<String>
        get() = _getOrderMatch

    private val _orderReplyLiveData = MutableLiveData<OltOrder>()
    override val orderReplyLiveData: LiveData<OltOrder>
        get() = _orderReplyLiveData

    private val _orderReplyList = mutableMapOf<String, OltOrder>()
    override val orderReplyList: MutableMap<String, OltOrder>
        get() = _orderReplyList

    private var _fastOrderLiveData = MutableLiveData<FastOrderListInfo>()
    override val fastOrderLiveData: LiveData<FastOrderListInfo>
        get() = _fastOrderLiveData

    private val _getOrderEipo = MutableLiveData<String>()
    override val getOrderEipo: LiveData<String>
        get() = _getOrderEipo

    private val _newOrderNotifLiveData = SingleLiveEvent<Boolean>()
    override val newOrderNotifLiveData: SingleLiveEvent<Boolean>
        get() = _newOrderNotifLiveData

    override suspend fun startNewOrderReply() {
        remoteSource.startNewOrderReply {
            when (it.type) {
                CakraMessage.Type.NEW_OLT_ORDER_ACK -> {
                    val newOltOrderAck = it.newOLTOrderAck

                    if (_orderReplyList.containsKey(newOltOrderAck.clOrderRef)) {
                        _orderReplyList[newOltOrderAck.clOrderRef]?.let { orderList ->
                            orderList.orderID = newOltOrderAck.orderID
                            orderList.status = newOltOrderAck.status
                        }
                    } else {
                        _orderReplyList[newOltOrderAck.clOrderRef] = OltOrder(
                            orderID = newOltOrderAck.orderID,
                            clOrderRef = newOltOrderAck.clOrderRef,
                            status = newOltOrderAck.status,
                            stockCode = newOltOrderAck.stockCode,
                            orderType = newOltOrderAck.orderType,
                            lotSize = newOltOrderAck.ordQty.div(100).toInt(),
                            ordPrice = newOltOrderAck.ordPrice,
                            buySell = newOltOrderAck.buySell,
                            timeInForce = newOltOrderAck.timeInForce
                        )
                    }
                    _newOrderNotifLiveData.postValue(true)
                    _orderReplyList[newOltOrderAck.clOrderRef]?.let { oltOrder ->


                    Handler(Looper.getMainLooper()).postDelayed({
                        _orderReplyLiveData.postValue(oltOrder)
                    }, 500)
                    }
                }

                CakraMessage.Type.NEW_OLT_ORDER_REJECT -> {
                    val newOLTOrderReject = it.newOLTOrderReject

                    _orderReplyList[newOLTOrderReject.clOrderRef]?.let { orderList ->
                        orderList.orderID = newOLTOrderReject.orderID
                        orderList.status = newOLTOrderReject.status
                        orderList.remarks = newOLTOrderReject.rejectReason
                    }

                    _orderReplyList[newOLTOrderReject.clOrderRef]?.let { oltOrder ->


                    Handler(Looper.getMainLooper()).postDelayed({
                        _orderReplyLiveData.postValue(oltOrder)
                    }, 500)
                    }

                }

                CakraMessage.Type.NEW_OLT_ORDER_EXCHANGE_UPDATE -> {
                    val newOLTOrderExchangeUpdate = it.newOLTOrderExchangeUpdate

                    _orderReplyList[newOLTOrderExchangeUpdate.clOrderRef]?.let { orderList ->
                        orderList.exOrderId = newOLTOrderExchangeUpdate.exOrderId
                        orderList.status = newOLTOrderExchangeUpdate.status
                    }

                    _orderReplyList[newOLTOrderExchangeUpdate.clOrderRef]?.let { oltOrder ->


                    Handler(Looper.getMainLooper()).postDelayed({
                        _orderReplyLiveData.postValue(oltOrder)
                    }, 500)
                    }
                }

                CakraMessage.Type.NEW_OLT_ORDER_PENDING_ACK -> {
                    val newOLTOrderPendingAck = it.newOLTOrderPendingAck

                    _orderReplyList[newOLTOrderPendingAck.clOrderRef]?.let { orderList ->
                        orderList.status = newOLTOrderPendingAck.status
                    }

                    _orderReplyList[newOLTOrderPendingAck.clOrderRef]?.let { oltOrder ->


                    Handler(Looper.getMainLooper()).postDelayed({
                        _orderReplyLiveData.postValue(oltOrder)
                    }, 500)
                    }
                }

                CakraMessage.Type.CANCEL_OLT_ORDER_REQUEST -> {
                    val cancelOLTOrderReject = it.cancelOLTOrderReject
                }

                CakraMessage.Type.CANCEL_OLT_ORDER_ACK -> {
                    val cancelOLTOrderAck = it.cancelOLTOrderAck

                    _orderReplyList[cancelOLTOrderAck.oldCliOrderRef]?.let { orderList ->
                        orderList.remarks = cancelOLTOrderAck.rejectReason
                        orderList.matchQty = cancelOLTOrderAck.mQty
                        orderList.status = cancelOLTOrderAck.status
                    }

                    _orderReplyList[cancelOLTOrderAck.oldCliOrderRef]?.let { oltOrder ->


                    Handler(Looper.getMainLooper()).postDelayed({
                        _orderReplyLiveData.postValue(oltOrder)
                    }, 500)
                    }
                }

                CakraMessage.Type.CANCEL_OLT_ORDER_REJECT -> {
                    val cancelOLTOrderReject = it.cancelOLTOrderReject

                    _orderReplyList[cancelOLTOrderReject.oldCliOrderRef]?.let { orderList ->
                        orderList.remarks = cancelOLTOrderReject.rejectReason
                        orderList.status = cancelOLTOrderReject.status
                    }

                    _orderReplyList[cancelOLTOrderReject.oldCliOrderRef]?.let { oltOrder ->


                    Handler(Looper.getMainLooper()).postDelayed({
                        _orderReplyLiveData.postValue(oltOrder)
                    }, 500)
                    }
                }

                CakraMessage.Type.AMEND_OLT_ORDER_REQUEST -> {
                    val amendOLTOrderRequest = it.amendOLTOrderRequest
                }

                CakraMessage.Type.AMEND_OLT_ORDER_ACK -> {
                    val amendOLTOrderAck = it.amendOLTOrderAck
                    _orderReplyList[amendOLTOrderAck.oldCliOrderRef]?.let { orderList ->
                        orderList.status = "A"
                    }

                    _orderReplyList[amendOLTOrderAck.newCliOrderRef]?.let { orderList ->
                        orderList.ordQty = amendOLTOrderAck.newQty
                        orderList.matchQty = amendOLTOrderAck.newMQty
                        orderList.orderID = amendOLTOrderAck.newCliOrderRef
                        orderList.exOrderId = amendOLTOrderAck.newOrderID
                        orderList.status = amendOLTOrderAck.status
                    }

                    _orderReplyList[amendOLTOrderAck.oldCliOrderRef]?.let { oltOrder ->


                    Handler(Looper.getMainLooper()).postDelayed({
                        _orderReplyLiveData.postValue(oltOrder)
                    }, 500)
                    }

                }

                CakraMessage.Type.AMEND_OLT_ORDER_REJECT -> {
                    val amendOLTOrderReject = it.amendOLTOrderReject

                    _orderReplyList[amendOLTOrderReject.oldCliOrderRef]?.let { orderList ->
                        orderList.status = amendOLTOrderReject.status
                    }

                    _orderReplyList[amendOLTOrderReject.newCliOrderRef]?.let { orderList ->
                        orderList.status = "R"
                        orderList.rejectReason = amendOLTOrderReject.rejectReason
                    }

                    _orderReplyList[amendOLTOrderReject.oldCliOrderRef]?.let { oltOrder ->


                    Handler(Looper.getMainLooper()).postDelayed({
                        _orderReplyLiveData.postValue(oltOrder)
                    }, 500)
                    }
                }

                CakraMessage.Type.EXECUTION_ACK -> {
                    val executionAck = it.executionAck

                    _orderReplyList[executionAck.cliOrderRef]?.let { orderList ->
                        orderList.exOrderId = executionAck.exOrderID
                        orderList.status = executionAck.status

                        if (executionAck.status == "M") {
                            _getOrderMatch.postValue(executionAck.stock)
                        }

                        val isExist = _orderReplyList.values.any { orderExecutionInfo ->
                            val exId = orderExecutionInfo.exOrderId
                            val tradeId = executionAck.tradeID.orEmpty()
                            exId == tradeId
                        }

                        if (!isExist) {
                            orderList.matchQty =
                                if (executionAck.hasTMQty() && executionAck.tmQty > 0) {
                                    executionAck.tmQty
                                } else {
                                    orderList.matchQty + executionAck.mQty
                                }

                            orderList.status =
                                if (orderList.matchQty < orderList.ordQty) "P" else "M"
                        }

                    }

                    _orderReplyList[executionAck.cliOrderRef]?.let { oltOrder ->


                    Handler(Looper.getMainLooper()).postDelayed({
                        _orderReplyLiveData.postValue(oltOrder)
                    }, 500)
                    }


                }

                CakraMessage.Type.FAST_ORDER_INFO_LIST -> {

                    val fastOrderListInfo = it.fastOrderListInfo
                    val fastOrderByStock = fastOrderListInfo.fastOrderByStockInfo
                    val buyFastOrderInfoList = mutableListOf<FastOrderInfo>()
                    val sellFastOrderInfoList = mutableListOf<FastOrderInfo>()

                    fastOrderListInfo.buyFastOrderInfoList
                        .map { data ->
                            buyFastOrderInfoList.add(
                                FastOrderInfo(
                                    data.price,
                                    data.totalOrder,
                                    data.totalOrdQty,
                                    data.relIdList,
                                    data.detailFastOrderInfoList.map { it.quantity })
                            )
                        }

                    fastOrderListInfo.sellFastOrderInfoList
                        .map { data ->
                            sellFastOrderInfoList.add(
                                FastOrderInfo(
                                    data.price,
                                    data.totalOrder,
                                    data.totalOrdQty,
                                    data.relIdList,
                                    data.detailFastOrderInfoList.map { it.quantity })
                            )
                        }

                    val data = FastOrderListInfo(
                        fastOrderListInfo.accNo,
                        fastOrderListInfo.stockCode,
                        buyFastOrderInfoList,
                        sellFastOrderInfoList,
                        FastOrderByStockInfo(
                            fastOrderByStock.buyingPowerCash,
                            fastOrderByStock.buyingPowerLimit,
                            fastOrderByStock.maxLotCash,
                            fastOrderByStock.maxLotLimit,
                            fastOrderByStock.maxLotSell,
                            fastOrderByStock.price,
                            )
                    )

                    _fastOrderLiveData.postValue(data)

                }

                CakraMessage.Type.EIPO_EQUITY_ORDER_ACK -> {
                    _getOrderEipo.postValue(it.ipoEquityOrderAck.ipoCode)
                }

                else -> {}
            }
        }
    }

    override suspend fun clearOrderReplyLiveData() {
//        orderReplyLiveData = MutableLiveData<OltOrder>()
    }

    override suspend fun insertOrderReply(orderReplyObject: OrderReplyObject) {
        remoteSource.insertOrderReply(orderReplyObject)
    }

    override suspend fun getOrderReplyDao(clOrderRef: String): Flow<Resource<OrderReplyObject>> =
        flow {
            emit(
                Resource.Success(
                    data = remoteSource.getOrderReplyDao(clOrderRef),
                    DataSource.REMOTE
                )
            )
        }

    override suspend fun putOrderReply(orderList: List<OrderInfo>) {
        withContext(Dispatchers.IO) {
            orderList.map { data ->
                val oltOrder = OltOrder(
                    orderID = data.odId,
                    clOrderRef = data.odId,
                    status = data.ostatus,
                    stockCode = data.stockcode,
                    orderType = data.ordertype,
                    lotSize = data.lotsize.toInt(),
                    ordPrice = data.oprice,
                    buySell = data.bs,
                    timeInForce = data.timeInForce
                )
                _orderReplyList[data.odId] = oltOrder
            }
        }
    }

    override suspend fun sendWithdrawFastOrder(cancelFastOrderReq: CancelFastOrderReq) {
        remoteSource.sendWithdrawFastOrder(cancelFastOrderReq)
    }

    override suspend fun sendAllWithdrawFastOrder(cancelFastOrderReq: CancelFastOrderReq) {
        remoteSource.sendAllWithdrawFastOrder(cancelFastOrderReq)
    }

    override suspend fun sendAmendFastOrder(amendFastOrderReq: AmendFastOrderReq) {
        remoteSource.sendAmendFastOrder(amendFastOrderReq)
    }

    override suspend fun getTradeListHistory(tradeListHistoryReq: TradeListHistoryReq): Flow<Resource<TradeListHistoryResponse?>> = flow{
        emit(Resource.Success(data = remoteSource.getTradeListHistory(tradeListHistoryReq), DataSource.REMOTE))
    }

    override suspend fun sendEipoOrder(eipoOrderRequest: EipoOrderRequest) {
        remoteSource.sendEipoOrder(eipoOrderRequest)
    }


    override suspend fun getTradeListHistoryGroup(tradeListHistoryReq: TradeListHistoryReq): Flow<Resource<TradeListHistoryGroupResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getTradeListHistoryGroup(tradeListHistoryReq), DataSource.REMOTE))
    }

    override suspend fun getTradeListHistoryGroupDetail(tradeListHistoryDetailReq: TradeListHistoryDetailReq): Flow<Resource<TradeListHistoryGroupDetailResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getTradeListHistoryGroupDetail(tradeListHistoryDetailReq), DataSource.REMOTE))
    }
}