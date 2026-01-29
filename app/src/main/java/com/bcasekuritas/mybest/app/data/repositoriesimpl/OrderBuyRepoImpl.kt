package com.bcasekuritas.mybest.app.data.repositoriesimpl

import com.bcasekuritas.mybest.app.domain.datasource.remote.OrderBuyDataSource
import com.bcasekuritas.mybest.app.domain.repositories.OrderBuyRepo
import com.bcasekuritas.mybest.ext.other.ErrorCodesMapper
import com.bcasekuritas.mybest.ext.provider.SchedulerProvider
import javax.inject.Inject

class OrderBuyRepoImpl @Inject constructor(
    private val remoteSource: OrderBuyDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val errorCodesMapper: ErrorCodesMapper
) : OrderBuyRepo {

}