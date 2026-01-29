package com.bcasekuritas.mybest.app.data.datasource.remote

import com.bcasekuritas.mybest.app.domain.datasource.remote.PriceAlertDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.AddPriceAlertReq
import com.bcasekuritas.mybest.app.domain.dto.request.PriceAlertReq
import com.bcasekuritas.mybest.app.domain.dto.request.RemovePriceAlertReq
import com.bcasekuritas.rabbitmq.network.OLTService
import com.bcasekuritas.rabbitmq.proto.bcas.AddPriceAlertRequest
import com.bcasekuritas.rabbitmq.proto.bcas.AddPriceAlertResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ListPriceAlertRequest
import com.bcasekuritas.rabbitmq.proto.bcas.ListPriceAlertResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RemovePriceAlertRequest
import com.bcasekuritas.rabbitmq.proto.bcas.RemovePriceAlertResponse
import javax.inject.Inject

class PriceAlertDataSourceImpl@Inject constructor(
    private val oltService: OLTService
) : PriceAlertDataSource {

    override suspend fun addPriceAlert(addPriceAlertRequest: AddPriceAlertReq): AddPriceAlertResponse? {
            val addPriceAlertReq = AddPriceAlertRequest.newBuilder()
                .setUserId(addPriceAlertRequest.userId)
                .setSessionId(addPriceAlertRequest.sessionId)
                .setStockCode(addPriceAlertRequest.stockCode)
                .setOperation(addPriceAlertRequest.operation)
                .setPrice(addPriceAlertRequest.price)
                .setId(addPriceAlertRequest.id)
                .build()

            return oltService.addPriceAlert(addPriceAlertReq)
    }

    override suspend fun getListPriceAlert(listPriceAlertRequest: PriceAlertReq): ListPriceAlertResponse? {
        val getListPriceAlertReq = ListPriceAlertRequest.newBuilder()
            .setUserId(listPriceAlertRequest.userId)
            .setSessionId(listPriceAlertRequest.sessionId)
            .setStockCode(listPriceAlertRequest.stockCode)
            .build()

        return oltService.getListPriceAlert(getListPriceAlertReq)
    }

    override suspend fun removePriceAlert(removePriceAlertReq: RemovePriceAlertReq): RemovePriceAlertResponse? {
        val removePriceAlertRequest = RemovePriceAlertRequest.newBuilder()
            .setUserId(removePriceAlertReq.userId)
            .setSessionId(removePriceAlertReq.sessionId)
            .setId(removePriceAlertReq.id)
            .build()

        return oltService.removePriceAlert(removePriceAlertRequest)
    }
}