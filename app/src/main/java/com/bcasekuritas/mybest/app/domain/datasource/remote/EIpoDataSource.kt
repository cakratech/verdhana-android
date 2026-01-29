package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.domain.dto.request.IPOInfoRequest
import com.bcasekuritas.rabbitmq.proto.bcas.PipelinesIpoListResponse
import com.bcasekuritas.mybest.app.domain.dto.request.IPOListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IPOOrderListRequest
import com.bcasekuritas.rabbitmq.proto.bcas.IpoOrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.PipelinesIpoInfoResponse

interface EIpoDataSource {
    suspend fun getIPOList(ipoListRequest: IPOListRequest):PipelinesIpoListResponse?
    suspend fun getIPOInfo(ipoInfoRequest: IPOInfoRequest):PipelinesIpoInfoResponse?
    suspend fun getIPOOrderList(ipoOrderListRequest: IPOOrderListRequest): IpoOrderListResponse?
}