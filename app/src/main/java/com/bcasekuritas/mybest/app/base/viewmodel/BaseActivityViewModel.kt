package com.bcasekuritas.mybest.app.base.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.bcasekuritas.mybest.app.data.entity.SessionObject
import com.bcasekuritas.mybest.app.domain.dto.request.OrderListRequest
import com.bcasekuritas.mybest.app.domain.dto.response.OltOrder
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioOrderItem
import com.bcasekuritas.mybest.app.domain.interactors.GetOrderListUseCase
import com.bcasekuritas.mybest.app.domain.interactors.InsertSessionDaoUseCase
import com.bcasekuritas.mybest.app.domain.interactors.UpdateSessionPinDaoUseCase
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.message.MQMessageListener
import com.bcasekuritas.rabbitmq.proto.bcas.AppNotification
import com.bcasekuritas.rabbitmq.proto.bcas.CakraMessage
import com.bcasekuritas.rabbitmq.proto.bcas.OrderListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BaseActivityViewModel @Inject constructor(
    private val insertSessionDaoUseCase: InsertSessionDaoUseCase,
    private val updateSessionPinDaoUseCase: UpdateSessionPinDaoUseCase,
    private val getOrderListUseCase: GetOrderListUseCase
) : ViewModel() {
    val getAppNotificationResult = MutableLiveData<AppNotification?>()
    val getOrderListResult = MutableLiveData<OrderListResponse?>()
    val getOrderNotifResult = MutableLiveData<OltOrder?>()
    var listOrderMap = mutableMapOf<String, OltOrder>()

    private var userIds = ""
    private var accNos = ""
    private var sessionIds = ""

    fun data(userId: String, accNo: String, sessionId: String) {
        userIds = userId
        accNos = accNo
        sessionIds = sessionId
    }

    private val appNotifListener =
        MQMessageListener<CakraMessage> { event ->
            if (event?.protoMsg?.type == CakraMessage.Type.APP_NOTIFICATION) {
                val appNotificationProto = event.protoMsg.appNotification
                getAppNotificationResult.postValue(appNotificationProto)
            }
        }

    private val orderReplyListener =
        MQMessageListener<CakraMessage> { event ->
            val cakraMessage = event?.protoMsg

            when (cakraMessage?.type) {
                CakraMessage.Type.NEW_OLT_ORDER_ACK -> {
                    val newOltOrderAck = cakraMessage.newOLTOrderAck

                    getOrderNotifResult.postValue(
                        OltOrder(
                            buySell = newOltOrderAck.buySell,
                            stockCode = newOltOrderAck.stockCode,
                            status = newOltOrderAck.status,
                            lotSize = newOltOrderAck.ordQty.div(100).toInt()
                            ,ordPrice = newOltOrderAck.ordPrice
                        )
                    )
                }
                CakraMessage.Type.NEW_OLT_ORDER_REJECT -> {}
                CakraMessage.Type.NEW_OLT_ORDER_PENDING_ACK -> {}
                CakraMessage.Type.CANCEL_OLT_ORDER_ACK -> {
                    val cancelOltOrderAck = cakraMessage.cancelOLTOrderAck

//                    getOrderNotifResult.postValue(
//                        OltOrder(
//                            buySell = cancelOltOrderAck.buySell,
//                            stockCode = cancelOltOrderAck.stock,
//                            status = cancelOltOrderAck.status,
//                            lotSize = cancelOltOrderAck.orderQty.div(100).toInt()
//                            ,ordPrice = cancelOltOrderAck.p
//                        )
//                    )
                }
                CakraMessage.Type.CANCEL_OLT_ORDER_REJECT -> {}
                CakraMessage.Type.AMEND_OLT_ORDER_REQUEST -> {}
                CakraMessage.Type.AMEND_OLT_ORDER_ACK -> {}
                CakraMessage.Type.AMEND_OLT_ORDER_REJECT -> {}
                CakraMessage.Type.NEW_OLT_ORDER_EXCHANGE_UPDATE -> {}
                CakraMessage.Type.EXECUTION_ACK -> {
                    val executionAck = cakraMessage.executionAck

                    getOrderNotifResult.postValue(
                        OltOrder(
                            buySell = executionAck.buySell,
                            stockCode = executionAck.stock,
                            status = executionAck.status,
                            lotSize = executionAck.ordQty.div(100).toInt()
                            ,ordPrice = executionAck.ordPrice
                        )
                    )
                }

                else -> {}
            }
            getOrderList(userIds, accNos, sessionIds, 0)
        }

    // AppNotification
//    fun startAppNotification() {
//        viewModelScope.launch(Dispatchers.IO) {
//            startAppNotificationUseCase.startAppNotification(appNotifListener)
//        }
//    }

    // AppNotification
//    fun subscribeAppNotification(sessionId: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            subscribeAppNotificationUseCase.subscribeAppNotification(sessionId)
//        }
//    }


    // AppNotification
//    fun UnSubscribeAppNotification(sessionId: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            unsubscribeAppNotificationUseCase.unsubscribeAppNotification(sessionId)
//        }
//    }



//    fun stopAppNotification(sessionId: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            stopAppNotificationUseCase.stopAppNotification(sessionId)
//        }
//    }

    // InsertSessionPin
    fun insertSession(sessionObject: SessionObject) {
        viewModelScope.launch(Dispatchers.IO) {
            insertSessionDaoUseCase.insertSessionDao(sessionObject)
        }
    }

    // Shared with orerList to update
    private val _orderListData = MutableLiveData<List<PortfolioOrderItem>>()
    val orderListData: LiveData<List<PortfolioOrderItem>> get() = _orderListData

    fun updateData(newData: List<PortfolioOrderItem>) {
        _orderListData.value = newData
    }

    fun getOrderList(userId: String, accNo: String, sessionId: String, includeTrade: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val orderListRequest = OrderListRequest(userId, accNo, sessionId, includeTrade)

            getOrderListUseCase.invoke(orderListRequest).collect() { resource ->
                resource.let {
                    when (it) {
                        is Resource.Success -> {
                            if (it.data != null) {
                                val orderListItemMapper = ArrayList<PortfolioOrderItem>()
                                it.data.ordersList?.map { data ->
                                    orderListItemMapper.add(
                                        PortfolioOrderItem(
                                            data.odId,
                                            data.exordid,
                                            data.odTime,
                                            data.ostatus,
                                            data.bs,
                                            data.ordertype,
                                            data.stockcode,
                                            data.remark,
                                            data.oprice,
                                            data.oqty,
                                            data.mqty
                                        )
                                    )
                                }
                                orderListItemMapper.sortBy { item -> item.time }
                                updateData(orderListItemMapper)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    /**  KODINGAN ORDER REPLY JIKA MAINTAIN LIST DATA DARI ORDER REPLY
     * when (cakraMessage?.type) {
     *                 CakraMessage.Type.NEW_OLT_ORDER -> {}
     *                 CakraMessage.Type.NEW_OLT_ORDER_ACK -> {
     *                     val newOltOrderAck = cakraMessage.newOLTOrderAck
     * //                    oltOrder = listOrder.firstOrNull { it?.clOrderRef == newOltOrderAck?.clOrderRef }
     *
     *                     listOrderMap[newOltOrderAck.orderID] = OltOrder(
     *                         orderID = newOltOrderAck.orderID,
     *                         orderTime = newOltOrderAck.orderTime,
     *                         status = newOltOrderAck.status,
     *                         buySell = newOltOrderAck.buySell,
     *                         orderType = newOltOrderAck.orderType,
     *                         stockCode = newOltOrderAck.stockCode,
     *                         remarks = newOltOrderAck.remark,
     *                         ordPrice = newOltOrderAck.ordPrice,
     *                         ordQty = newOltOrderAck.ordQty
     *
     *                     )
     *
     *                 }
     *                 CakraMessage.Type.NEW_OLT_ORDER_REJECT -> {
     *                     val newOltOrderReject = cakraMessage.newOLTOrderReject
     *
     *                     newOltOrderReject.orderID?.let { orderId ->
     *                         listOrderMap[orderId]?.let {
     *                             it.orderID = newOltOrderReject.orderID
     *                             it.status = newOltOrderReject.status
     *                             it.remarks = newOltOrderReject.rejectReason
     *                         }
     *                     }
     *                 }
     *                 CakraMessage.Type.NEW_OLT_ORDER_PENDING_ACK -> {
     *                     val newOltOrderPendingAck = cakraMessage.newOLTOrderPendingAck
     *
     *                     newOltOrderPendingAck.orderID?.let { orderId ->
     *                         listOrderMap[orderId]?.status = cakraMessage.newOLTOrderPendingAck.status
     *                     }
     *                 }
     *                 CakraMessage.Type.CANCEL_OLT_ORDER_REQUEST -> {}
     *                 CakraMessage.Type.CANCEL_OLT_ORDER_ACK -> {
     *                     val cancelOltOrderAck = cakraMessage.cancelOLTOrderAck
     *
     *                     cancelOltOrderAck.oldOrderID?.let { oldOrderId ->
     *                         listOrderMap[oldOrderId]?.let {
     *                             it.remarks = cancelOltOrderAck.rejectReason
     *                             it.matchQty = cancelOltOrderAck.mQty
     *                             it.status = cancelOltOrderAck.status
     *                         }
     *                     }
     *                 }
     *                 CakraMessage.Type.CANCEL_OLT_ORDER_REJECT -> {
     *                     val cancelOltOrderReject = cakraMessage.cancelOLTOrderReject
     *
     *                     cancelOltOrderReject.orderID?.let { orderId ->
     *                         listOrderMap[orderId]?.let {
     *                             it.status = cancelOltOrderReject.status
     *                             it.remarks = cancelOltOrderReject.rejectReason
     *                         }
     *                     }
     *                 }
     *                 CakraMessage.Type.AMEND_OLT_ORDER_REQUEST -> {}
     *                 CakraMessage.Type.AMEND_OLT_ORDER_ACK -> {
     *                     val amendOltOrderAck = cakraMessage.amendOLTOrderAck
     *
     *                     amendOltOrderAck.oldOrderID?.let { oldOrderId ->
     *                         listOrderMap[oldOrderId]?.let {
     *                             it.status = "A"
     *                         }
     *                     }
     *
     *                     amendOltOrderAck.newOrderID?.let { newOrderId ->
     *                         listOrderMap[newOrderId]?.let {
     *                             it.ordQty = amendOltOrderAck.newQty
     *                             it.matchQty = amendOltOrderAck.newMQty
     *                             it.orderID = amendOltOrderAck.newCliOrderRef
     *                             it.exOrderId = amendOltOrderAck.newOrderID
     *                         }
     *                     }
     *                 }
     *                 CakraMessage.Type.AMEND_OLT_ORDER_REJECT -> {
     *                     val amendOltOrderReject = cakraMessage.amendOLTOrderReject
     *
     *
     *                     amendOltOrderReject.oldCliOrderRef?.let { oldCliOrdRef ->
     *                         listOrderMap[oldCliOrdRef]?.let {
     *                             it.status = amendOltOrderReject.status
     *                         }
     *                     }
     *
     *                     amendOltOrderReject.newCliOrderRef?.let { newCliOrdRef ->
     *                         listOrderMap[newCliOrdRef]?.let {
     *                             it.status = "R"
     *                             it.remarks = amendOltOrderReject.rejectReason
     *                         }
     *                     }
     *
     *                 }
     *                 CakraMessage.Type.NEW_OLT_ORDER_EXCHANGE_UPDATE -> {
     *                     val newOltOrderExchange = cakraMessage.newOLTOrderExchangeUpdate
     *
     *                     newOltOrderExchange.orderID?.let { orderId ->
     *                         listOrderMap[orderId]?.let {
     *                             it.exOrderId = newOltOrderExchange.exOrderId
     *                             it.status = newOltOrderExchange.status
     *                         }
     *                     }
     *                 }
     *                 CakraMessage.Type.EXECUTION_ACK -> {
     *                     val executionAck = cakraMessage.executionAck
     *
     *                     executionAck.orderID?.let { orderId ->
     *                         listOrderMap[orderId]?.let {
     *                             it.exOrderId = executionAck.exOrderID
     *                             it.status = executionAck.status
     *                         }
     *                     }
     *                 }
     *                 else -> {}
     *             }
     * */
}