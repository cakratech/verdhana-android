package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.EIpoDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.IPOInfoRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IPOListRequest
import com.bcasekuritas.mybest.app.domain.dto.request.IPOOrderListRequest
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.bcas.IpoOrderListRequest
import com.bcasekuritas.rabbitmq.proto.bcas.IpoOrderListResponse
import com.bcasekuritas.rabbitmq.proto.bcas.PipelinesIpoInfoRequest
import com.bcasekuritas.rabbitmq.proto.bcas.PipelinesIpoInfoResponse
import com.bcasekuritas.rabbitmq.proto.bcas.PipelinesIpoListRequest
import com.bcasekuritas.rabbitmq.proto.bcas.PipelinesIpoListResponse
import javax.inject.Inject

class EIpoDataSourceImpl @Inject constructor(
    private val oltService: OLTService
) : EIpoDataSource {

    override suspend fun getIPOList(ipoListRequest: IPOListRequest): PipelinesIpoListResponse? {
        val request = PipelinesIpoListRequest.newBuilder()
            .setUserId(ipoListRequest.userId)
            .setSessionId(ipoListRequest.sessionId)
            .setIsBookBuilding(ipoListRequest.isBookBuilding)
            .setSort(ipoListRequest.sort)
//            .setSizeItem(ipoListRequest.sizeItem)
//            .setPage(ipoListRequest.page)
            .build()

        return oltService.getEIPOList(request)
    }

    override suspend fun getIPOInfo(ipoInfoRequest: IPOInfoRequest): PipelinesIpoInfoResponse? {
        val request = PipelinesIpoInfoRequest.newBuilder()
            .setUserId(ipoInfoRequest.userId)
            .setSessionId(ipoInfoRequest.sessionId)
            .setIpoCode(ipoInfoRequest.ipoCode)
            .build()

        return oltService.getEIPOInfo(request)
    }

    override suspend fun getIPOOrderList(ipoOrderListRequest: IPOOrderListRequest): IpoOrderListResponse? {
        val request = IpoOrderListRequest.newBuilder().apply {
            setUserId(ipoOrderListRequest.userId)
            setSessionId(ipoOrderListRequest.sessionId)
            setAccno(ipoOrderListRequest.accNo)
            setIpoCode(ipoOrderListRequest.ipoCode)
            setSort(ipoOrderListRequest.sort)
            setSizeItem(ipoOrderListRequest.sizeItem)
            setPage(ipoOrderListRequest.page)
        }.build()

        return oltService.getEIPOOrderList(request)
    }
}