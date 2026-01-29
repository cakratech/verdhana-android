package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.domain.dto.request.FastOrderListReq
import com.bcasekuritas.mybest.app.domain.dto.request.PublishFastOrderReq
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.FastOrderListResponse
import kotlinx.coroutines.flow.Flow

interface FastOrderRepo {
    suspend fun getFastOrderList(fastOrderListReq: FastOrderListReq): Flow<Resource<FastOrderListResponse?>>
    suspend fun publishFastOrder(publishFastOrderReq: PublishFastOrderReq?)
}