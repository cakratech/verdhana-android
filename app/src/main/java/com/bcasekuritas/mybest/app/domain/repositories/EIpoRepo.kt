package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.domain.dto.request.IPOInfoRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IPOListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IPOOrderListRequest
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.IpoOrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.PipelinesIpoInfoResponse
import com.bcasekuritas.rabbitmq.proto.bcas.PipelinesIpoListResponse
import kotlinx.coroutines.flow.Flow

interface EIpoRepo {
    suspend fun getIPOList(ipoListRequest: IPOListRequest): Flow<Resource<PipelinesIpoListResponse?>>
    suspend fun getIPOInfo(ipoInfoRequest: IPOInfoRequest): Flow<Resource<PipelinesIpoInfoResponse?>>
    suspend fun getIPOOrderList(ipoOrderListRequest: IPOOrderListRequest): Flow<Resource<IpoOrderListResponse?>>
}