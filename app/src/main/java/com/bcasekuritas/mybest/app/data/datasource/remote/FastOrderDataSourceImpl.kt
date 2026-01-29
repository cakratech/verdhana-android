package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.FastOrderDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.FastOrderListReq
import com.bcasekuritas.mybest.app.domain.dto.request.PublishFastOrderReq
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.bcas.CakraMessage
import com.bcasekuritas.rabbitmq.proto.bcas.FastOrderListRequest
import com.bcasekuritas.rabbitmq.proto.bcas.FastOrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.SubsFastOrder
import javax.inject.Inject

class FastOrderDataSourceImpl  @Inject constructor(
    private val oltService: OLTService
) : FastOrderDataSource {
    override suspend fun getFastOrderList(fastOrderListReq: FastOrderListReq): FastOrderListResponse? {
        val fastOrderReq = FastOrderListRequest
            .newBuilder()
            .setUserId(fastOrderListReq.userId)
            .setAccNo(fastOrderListReq.accNo)
            .setSessionId(fastOrderListReq.sessionId)
            .setStockCode(fastOrderListReq.stockCode)
            .build()

        return oltService.getFastOrderList(fastOrderReq)
    }

    override suspend fun publishFastOrder(publishFastOrderReq: PublishFastOrderReq?) {

        val subsFastOrder = SubsFastOrder
            .newBuilder()
            .setUserId(publishFastOrderReq?.userId)
            .setSessionId(publishFastOrderReq?.sessionId)
            .setSubsOp(publishFastOrderReq?.subsOp ?: 1)
            .setAccNo(publishFastOrderReq?.accNo)
            .setStockCode(publishFastOrderReq?.stockCode)


        val cakraMessage = CakraMessage
            .newBuilder()
            .setType(CakraMessage.Type.SUBSCRIBE_FAST_ORDER)
            .setSubsFastOrder(subsFastOrder)
            .setSessionId(subsFastOrder.sessionId)
            .build()

        return oltService.publishSubsInfo(cakraMessage)
    }
}