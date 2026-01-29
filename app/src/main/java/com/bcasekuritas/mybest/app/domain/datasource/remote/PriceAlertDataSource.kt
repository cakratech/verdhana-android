package com.bcasekuritas.mybest.app.domain.datasource.remote

import com.bcasekuritas.mybest.app.domain.dto.request.AddPriceAlertReq
import com.bcasekuritas.mybest.app.domain.dto.request.PriceAlertReq
import com.bcasekuritas.mybest.app.domain.dto.request.RemovePriceAlertReq
import com.bcasekuritas.rabbitmq.proto.bcas.AddPriceAlertResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ListPriceAlertResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RemovePriceAlertResponse

interface PriceAlertDataSource {

    suspend fun addPriceAlert(addPriceAlertRequest: AddPriceAlertReq): AddPriceAlertResponse?
    suspend fun getListPriceAlert(listPriceAlertRequest: PriceAlertReq): ListPriceAlertResponse?
    suspend fun removePriceAlert(removePriceAlertReq: RemovePriceAlertReq): RemovePriceAlertResponse?
}