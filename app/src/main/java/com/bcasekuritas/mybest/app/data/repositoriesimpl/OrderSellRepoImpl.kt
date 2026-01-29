package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.OrderSellDataSource
import com.bcasekuritas.mybest.app.domain.repositories.OrderSellRepo
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import javax.inject.Inject

class OrderSellRepoImpl @Inject constructor(
    private val remoteSource: OrderSellDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : OrderSellRepo {

}