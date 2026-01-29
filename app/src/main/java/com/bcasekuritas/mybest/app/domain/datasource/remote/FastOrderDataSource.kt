package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.domain.dto.request.FastOrderListReq
import com.bcasekuritas.mybest.app.domain.dto.request.PublishFastOrderReq
import com.bcasekuritas.rabbitmq.proto.bcas.FastOrderListResponse

interface FastOrderDataSource {
    suspend fun getFastOrderList(fastOrderListReq: FastOrderListReq): FastOrderListResponse?

    suspend fun publishFastOrder(publishFastOrderReq: PublishFastOrderReq?)
}