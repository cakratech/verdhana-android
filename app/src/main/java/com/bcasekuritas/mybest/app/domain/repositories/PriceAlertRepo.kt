package com.bcasekuritas.mybest.app.domain.repositories

import com.bcasekuritas.mybest.app.domain.dto.request.AddPriceAlertReq
import com.bcasekuritas.mybest.app.domain.dto.request.PriceAlertReq
import com.bcasekuritas.mybest.app.domain.dto.request.RemovePriceAlertReq
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.AddPriceAlertResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ListPriceAlertResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RemovePriceAlertResponse
import kotlinx.coroutines.flow.Flow

interface PriceAlertRepo {

    suspend fun addPriceAlert(addPriceAlertRequest: AddPriceAlertReq): Flow<Resource<AddPriceAlertResponse?>>
    suspend fun getListPriceAlert(listPriceAlertRequest: PriceAlertReq): Flow<Resource<ListPriceAlertResponse?>>
    suspend fun removePriceAlert(removePriceAlertReq: RemovePriceAlertReq): Flow<Resource<RemovePriceAlertResponse?>>
}