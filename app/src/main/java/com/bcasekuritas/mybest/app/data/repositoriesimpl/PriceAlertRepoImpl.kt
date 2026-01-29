package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.PriceAlertDataSource
import com.bcasekuritas.mybest.app.domain.dto.request.AddPriceAlertReq
import com.bcasekuritas.mybest.app.domain.dto.request.PriceAlertReq
import com.bcasekuritas.mybest.app.domain.dto.request.RemovePriceAlertReq
import com.bcasekuritas.mybest.app.domain.repositories.PriceAlertRepo
import com.bcasekuritas.mybest.app.domain.subscribers.DataSource
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.rabbitmq.proto.bcas.AddPriceAlertResponse
import com.bcasekuritas.rabbitmq.proto.bcas.ListPriceAlertResponse
import com.bcasekuritas.rabbitmq.proto.bcas.RemovePriceAlertResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PriceAlertRepoImpl @Inject constructor(
    private val remoteSource: PriceAlertDataSource
): PriceAlertRepo {
    override suspend fun addPriceAlert(addPriceAlertRequest: AddPriceAlertReq): Flow<Resource<AddPriceAlertResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.addPriceAlert(addPriceAlertRequest), DataSource.REMOTE))
    }

    override suspend fun getListPriceAlert(listPriceAlertRequest: PriceAlertReq): Flow<Resource<ListPriceAlertResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.getListPriceAlert(listPriceAlertRequest), DataSource.REMOTE))
    }

    override suspend fun removePriceAlert(removePriceAlertReq: RemovePriceAlertReq): Flow<Resource<RemovePriceAlertResponse?>> = flow {
        emit(Resource.Success(data = remoteSource.removePriceAlert(removePriceAlertReq), DataSource.REMOTE))
    }
}